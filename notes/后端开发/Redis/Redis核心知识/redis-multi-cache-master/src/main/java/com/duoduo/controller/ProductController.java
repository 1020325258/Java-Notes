package com.duoduo.controller;

import com.duoduo.common.RedisUtil;
import com.duoduo.model.Product;
import com.duoduo.service.ProductService;
import org.redisson.Redisson;
import org.redisson.api.RBitSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.BitSet;

@RestController
public class ProductController {

    @Autowired
    private ProductService productService;

    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public Product create(@RequestBody Product productParam) {
        return productService.create(productParam);
    }

    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public Product update(@RequestBody Product productParam) {
        return productService.update(productParam);
    }

    @RequestMapping("/get/{productId}")
    public Product getProduct(@PathVariable Long productId) {
        return productService.get(productId);
    }

    @Autowired
    Redisson redisson;


    @GetMapping("/testRedis")
    public void testRedis() {
        RBitSet USERA = redisson.getBitSet("userx:202310");
        int day = LocalDateTime.now().getDayOfMonth();
        USERA.set(day);
        System.out.println("userx签到成功，值为：" + USERA.get(day));
        // 假设昨天前天也签到了
        USERA.set(day-1);
        USERA.set(day-2);
        for (int i = day; i >= 1; i --) {
            USERA.toString();
        }
    }
}