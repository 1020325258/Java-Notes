# Redis实现订阅发布

Redis  可以通过订阅发布功能来实现动态接收消息的功能

场景：如果服务提供者新提供了一些接口供消费者使用，这两个服务并不在一个模块下，怎么可以让消费者动态的感知到提供者新添加的接口。



实现订阅发布功能，需要两个模块

## 订阅模块

订阅模块新建为 SpringBoot 项目，引入 jedis 依赖

### 引入依赖

```xml
    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter</artifactId>
        </dependency>

        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-devtools</artifactId>
            <scope>runtime</scope>
            <optional>true</optional>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
        <!-- 添加redis依赖模块 -->
        <dependency>
            <groupId>redis.clients</groupId>
            <artifactId>jedis</artifactId>
<!--            <version>2.9.0</version>-->
            <version>3.3.0</version>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-redis</artifactId>
            <version>2.0.6.RELEASE</version>
        </dependency>
    </dependencies>
```



### 自动配置类

在这里配置 redis 的连接，以及 redis 订阅 topic，这里订阅 topic 为 `test-redis-push`，并且指定接受方法为 Receiver 的 receiveMessage 方法

```java
@Configuration
public class AutoConfig {
    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        // 1. 拉取注册中心的 Redis 配置信息
        Map<String, String> redisConfig = new HashMap<>();
        redisConfig.put("host", "127.0.0.1");
        redisConfig.put("port", "6379");
        // 2. 构建 Redis 服务
        RedisStandaloneConfiguration standaloneConfig = new RedisStandaloneConfiguration();
        standaloneConfig.setHostName(redisConfig.get("host"));
        standaloneConfig.setPort(Integer.parseInt(redisConfig.get("port")));
        // 3. 默认配置信息；一般这些配置可以被抽取出来
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxTotal(100);
        poolConfig.setMaxWaitMillis(30 * 1000);
        poolConfig.setMinIdle(20);
        poolConfig.setMaxIdle(40);
        poolConfig.setTestWhileIdle(true);
        // 4. 创建 Redis 配置
        JedisClientConfiguration clientConfig = JedisClientConfiguration.builder()
                .connectTimeout(Duration.ofSeconds(2))
                .clientName("api-gateway-assist-redis-nginx-test3")
                .usePooling().poolConfig(poolConfig).build();
        // 5. 实例化 Redis 链接对象
        return new JedisConnectionFactory(standaloneConfig, clientConfig);
    }

    @Bean
    public RedisMessageListenerContainer container(RedisConnectionFactory redisConnectionFactory, MessageListenerAdapter msgAgreementListenerAdapter) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(redisConnectionFactory);
        //  container.addMessageListener(msgAgreementListenerAdapter, new PatternTopic("api-gateway-g4"));
        container.addMessageListener(msgAgreementListenerAdapter, new PatternTopic("test-redis-push"));
        return container;
    }

    @Bean
    public MessageListenerAdapter msgAgreementListenerAdapter(Receiver receiver) {
        return new MessageListenerAdapter(receiver, "receiveMessage");
    }

    @Bean
    public Receiver receiver() {
        return new Receiver();
    }

}
```



### 接收者

```java
public class Receiver {
    private Logger logger = LoggerFactory.getLogger(Receiver.class);

    public void receiveMessage(Object message) {
        logger.info("接受Redis推送消息 message：{}", message);
    }

}
```





## 发布模块

### 引入依赖

引入 redis 相关依赖

```xml
<dependencies>
    <dependency>
        <groupId>junit</groupId>
        <artifactId>junit</artifactId>
        <version>4.13.2</version>
        <scope>test</scope>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-redis</artifactId>
        <version>2.0.6.RELEASE</version>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-test</artifactId>
        <scope>test</scope>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-devtools</artifactId>
        <scope>runtime</scope>
        <optional>true</optional>
    </dependency>
</dependencies>
```



### 自动配置类

在这里 RedisConnectionFactory 回去读取 yaml 的 redis 配置，可以在 yaml 中配置 redis 地址

```java
@Configuration
public class AutoConfig {
    @Bean
    public RedisTemplate<String, Object> redisMessageTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        template.setDefaultSerializer(new FastJsonRedisSerializer<>(Object.class));
        return template;
    }
}
```



### yaml

```yaml
server:
  port: 8000

spring:
  redis:
    host: 127.0.0.1
    port: 6379
```



### 发布者



```java
@Service
public class Publisher {

    private final RedisTemplate<String, Object> redisMessageTemplate;

    @Autowired
    public Publisher(RedisTemplate<String, Object> redisMessageTemplate) {
        this.redisMessageTemplate = redisMessageTemplate;
    }

    public void pushMessage(String topic, Object message) {
        redisMessageTemplate.convertAndSend(topic, message);
    }
}

```



### 测试：发送消息

先启动接收者，并在 springboot 的测试模块中写入以下代码，可以观察接收者控制台打印。

```java
@SpringBootTest
public class RedisPublishTest {
    @Resource
    private Publisher publisher;

    @Test
    public void test() {
        publisher.pushMessage("test-redis-push", "data");
    }
}
```

