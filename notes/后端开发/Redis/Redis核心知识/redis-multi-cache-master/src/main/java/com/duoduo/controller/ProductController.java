package com.duoduo.controller;

import com.duoduo.common.RedisUtil;
import com.duoduo.model.Product;
import com.duoduo.service.ProductService;
import org.redisson.Redisson;
import org.redisson.api.RBitSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Array;
import java.time.LocalDateTime;
import java.util.*;

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
        RBitSet USERA = redisson.getBitSet("userc:202310");
        int day = LocalDateTime.now().getDayOfMonth();
        USERA.set(day);
        System.out.println("userx签到成功，值为：" + USERA.get(day));
        // 假设昨天前天也签到了
        USERA.set(day-1);
        USERA.set(day-2);
        BitSet bs = USERA.asBitSet();
        Map<Integer, Boolean> map = new HashMap<>();
        for (int i = bs.nextSetBit(0); i >= 0; i = bs.nextSetBit(i+1)) {
            // operate on index i here
            if (i == Integer.MAX_VALUE) {
                break; // or (i+1) would overflow
            }
            map.put(i, true);
        }
        System.out.println("签到记录：" + map.toString());
        System.out.println("连续签到天数：" + getContinousSignDays(bs));
    }

    public int getContinousSignDays(BitSet bs) {
        List<Integer> list = new ArrayList<>();
        for (int i = bs.nextSetBit(0); i >= 0; i = bs.nextSetBit(i+1)) {
            // operate on index i here
            if (i == Integer.MAX_VALUE) {
                break; // or (i+1) would overflow
            }
            list.add(i);
        }
        int day = LocalDateTime.now().getDayOfMonth();
        int lastDay = day;
        int continousSignDay = 0;
        if (list.get(list.size() - 1) == day) {
            for (int i = list.size() - 1; i >= 0; i --) {
                if (list.get(i) == lastDay) {
                    continousSignDay ++;
                    lastDay --;
                } else {
                    break;
                }
            }
        }
        return continousSignDay;
    }
}