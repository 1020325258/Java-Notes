package com.duoduo.service;

import com.alibaba.fastjson.JSON;
import com.duoduo.common.RedisKeyPrefixConst;
import com.duoduo.common.RedisUtil;
import com.duoduo.dao.ProductDao;
import com.duoduo.model.Product;
import org.redisson.Redisson;
import org.redisson.api.RLock;
import org.redisson.api.RReadWriteLock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Service
public class ProductService {

    @Autowired
    private ProductDao productDao;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private Redisson redisson;

    public static final Integer PRODUCT_CACHE_TIMEOUT = 60 * 60 * 24;
    // 空缓存，防止缓存穿透，当访问到了数据库中不存在的数据，就使用空缓存来表示
    // 防止黑客攻击，黑客可能会大量访问数据库不存在的数据来进行攻击
    public static final String EMPTY_CACHE = "{}";
    public static final String LOCK_PRODUCT_HOT_CACHE_CREATE_PREFIX = "lock:product:hot_cache_create:";
    public static final String LOCK_PRODUCT_UPDATE_PREFIX = "lock:product:update:";

    /**
     * 多级缓存：redis单机可以每秒接收10w次的请求，JVM缓存比redis缓存可以抗更高数量级的并发，JVM缓存可以每秒接收百万的请求，多加几台web应用也可以进一步提高并发，因此可以再加一级JVM缓存
     * 缺点：
     * （1）JVM缓存有容量限制，很容易达到内存上限，而且如果某些热点数据之后不怎么用了，但是一直在JVM缓存中存储，可能会导致内存泄漏
     * （2）如果web应用是集群的，如果一台服务器中的jvm缓存数据更新了，会造成集群web中数据不一致
     * 尝试解决：（1）使用ehcache、guava等jvm进程级别缓存框架，一般都会处理缓存的过期
     *      （2）使用rocketMQ将修改的数据发送到queue中，其他web进程订阅，有修改的话，进行修改，这样做太麻烦了，一般不使用，因为太麻烦了
     *
     * 大厂方案：一般会有一个专门的缓存系统，这个缓存系统与web集群相连接，计算哪些是热点数据，在缓存系统中处理这些数据的缓存
     * 注意，只有访问量非常大的数据（每秒几十万）才会放入jvm缓存中
     *
     */
    public static Map<String, Product> productMap = new HashMap<>();

    @Transactional
    public Product create(Product product) {
//        redisson.
        Product productResult = productDao.create(product);
        redisUtil.set(RedisKeyPrefixConst.PRODUCT_CACHE + productResult.getId(), JSON.toJSONString(productResult));
        return productResult;
    }

    @Transactional
    public Product update(Product product) {
        Product productResult = null;
//        RLock productUpdateLock = redisson.getLock(LOCK_PRODUCT_UPDATE_PREFIX + product.getId());
        RReadWriteLock productUpdateLock = redisson.getReadWriteLock(LOCK_PRODUCT_UPDATE_PREFIX + product.getId());
        RLock writeLock = productUpdateLock.writeLock();
        //加分布式写锁解决缓存双写不一致问题
        writeLock.lock();
        try {
            productResult = productDao.update(product);
            redisUtil.set(RedisKeyPrefixConst.PRODUCT_CACHE + productResult.getId(), JSON.toJSONString(productResult),
                    genProductCacheTimeout(), TimeUnit.SECONDS);
        } finally {
            writeLock.unlock();
        }
        return productResult;
    }

    public Product get(Long productId) {
        Product product = null;
        String productCacheKey = RedisKeyPrefixConst.PRODUCT_CACHE + productId;

        //从缓存里查数据
        product = getProductFromCache(productCacheKey);
        if (product != null) {
            return product;
        }
        // RBloomFilter<Object> bloomFilter = redisson.getBloomFilter("");
        //初始化布隆过滤器：预计元素为100000000L,误差率为3%,根据这两个参数会计算出底层的bit数组大小
        // bloomFilter.tryInit(10000L,0.03);
        // bloomFilter.add("aaa");
        //加分布式锁解决热点缓存并发重建问题
        RLock hotCreateCacheLock = redisson.getLock(LOCK_PRODUCT_HOT_CACHE_CREATE_PREFIX + productId);
        hotCreateCacheLock.lock();

        // 这个优化谨慎使用，防止超时导致的大规模并发重建问题
        // hotCreateCacheLock.tryLock(1, TimeUnit.SECONDS);

        try {
            //双重检测
            /**
             * 这里进行双重检测的目的：在主播带货时，可能主播会介绍一些比较冷门但是好用的东西，如果此时有大量用户去访问这些数据的话，那么这些冷门数据就会变为热点数据
             * 出现的问题：大量请求访问冷门数据，而冷门数据没有进行缓存，大量请求访问数据库，数据库可能就崩了
             * 如何解决：双重检测，先去缓存中取，如果没有的话，加上分布式锁，再次尝试去缓存中取（因为可能其他线程在这个时候已经在数据库中查询数据放入缓存中去了），如果没有，去数据库中查询，查询之后将数据放到缓存中，分布式锁解锁
             */
            product = getProductFromCache(productCacheKey);


            if (product != null) {
                return product;
            }

            /**
             * 可以使用分布式锁来解决数据库缓存双写不一致问题
             * 但是如果对于读多写少的应用程序来说，可以采用读写锁来进一步优化：读写互斥、读读不互斥
             */
            //RLock productUpdateLock = redisson.getLock(LOCK_PRODUCT_UPDATE_PREFIX + productId);
            RReadWriteLock productUpdateLock = redisson.getReadWriteLock(LOCK_PRODUCT_UPDATE_PREFIX + productId);
            RLock rLock = productUpdateLock.readLock();
            //加分布式读锁解决数据库缓存双写不一致问题
            rLock.lock();
            try {
                product = productDao.get(productId);
                if (product != null) {
                    // 放入redis缓存
                    redisUtil.set(productCacheKey, JSON.toJSONString(product),
                            genProductCacheTimeout(), TimeUnit.SECONDS);
                } else {
                    //设置空缓存解决缓存穿透问题
                    redisUtil.set(productCacheKey, EMPTY_CACHE, genEmptyCacheTimeout(), TimeUnit.SECONDS);
                }
            } finally {
                rLock.unlock();
            }
        } finally {
            hotCreateCacheLock.unlock();
        }

        return product;
    }


    private Integer genProductCacheTimeout() {
        //加随机超时机制解决缓存批量失效(击穿)问题
        //缓存击穿：大量缓存同时失效，导致大量请求直达数据库
        //这里将超时时间设置为24h+随机时间就是为了防止批量添加商品时，过期时间相同，导致某个时间点大量缓存失效
        return PRODUCT_CACHE_TIMEOUT + new Random().nextInt(5) * 60 * 60;
    }

    // 空缓存时间设置短一点，防止大量空缓存占用redis空间
    private Integer genEmptyCacheTimeout() {
        return 60 + new Random().nextInt(30);
    }

    private Product getProductFromCache(String productCacheKey) {
        Product product = null;
        //多级缓存查询，jvm级别缓存可以交给单独的热点缓存系统统一维护，有变动推送到各个web应用系统自行更新
        product = productMap.get(productCacheKey);
        if (product != null) {
            return product;
        }
        String productStr = redisUtil.get(productCacheKey);
        if (!StringUtils.isEmpty(productStr)) {
            // 取到的是空缓存
            if (EMPTY_CACHE.equals(productStr)) {
                redisUtil.expire(productCacheKey, genEmptyCacheTimeout(), TimeUnit.SECONDS);
                // 如果是空缓存的话，返回一个空的Product
                return new Product();
            }
            product = JSON.parseObject(productStr, Product.class);
            // 缓存读延期，实现简单的冷热分离，对需要读的数据延长过期时间
            redisUtil.expire(productCacheKey, genProductCacheTimeout(), TimeUnit.SECONDS);
        }
        return product;
    }

}