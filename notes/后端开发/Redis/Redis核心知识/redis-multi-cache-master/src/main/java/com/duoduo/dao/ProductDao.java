package com.duoduo.dao;

import com.duoduo.model.Product;
import org.springframework.stereotype.Repository;

/**
 * 只是演示示例代码，没有真正操作数据库
 */
@Repository
public class ProductDao {

    public Product create(Product product) {
        System.out.println("创建商品成功");
        return product;
    }

    public Product update(Product product) {
        System.out.println("修改商品成功");
        return product;
    }

    public Product get(Long productId) {
        System.out.println("查询商品成功");
        Product product = new Product();
        product.setId(productId);
        product.setName("test");
        return product;
    }

}
