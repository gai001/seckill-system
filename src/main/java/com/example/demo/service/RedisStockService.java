package com.example.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class RedisStockService {

    @Autowired
    private StringRedisTemplate redisTemplate;

    private static final String STOCK_KEY = "product:stock:%d";

    // 初始化库存（项目启动时调用）
    public void initStock(Integer productId, Integer stock) {
        String key = String.format(STOCK_KEY, productId);
        redisTemplate.opsForValue().set(key, String.valueOf(stock));
    }

    // 原子扣减库存（Lua 脚本）
    public Long decrStock(Integer productId, Integer quantity) {
        String key = String.format(STOCK_KEY, productId);
        String script =
            "local stock = tonumber(redis.call('get', KEYS[1])) " +
            "if stock == nil then return -2 end " +
            "if stock >= tonumber(ARGV[1]) then " +
            "    redis.call('decrby', KEYS[1], ARGV[1]) " +
            "    return stock - tonumber(ARGV[1]) " +
            "else " +
            "    return -1 " +
            "end";
        DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>(script, Long.class);
        return redisTemplate.execute(redisScript, Collections.singletonList(key), quantity.toString());
    }

    // 获取当前库存
    public Integer getStock(Integer productId) {
        String key = String.format(STOCK_KEY, productId);
        String val = redisTemplate.opsForValue().get(key);
        return val == null ? null : Integer.valueOf(val);
    }
}