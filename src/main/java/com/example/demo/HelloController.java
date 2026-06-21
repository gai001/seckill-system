package com.example.demo;

import com.example.demo.entity.Order;
import com.example.demo.entity.Product;
import com.example.demo.mapper.OrderMapper;
import com.example.demo.mapper.ProductMapper;
import com.example.demo.service.RedisStockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;

@RestController
public class HelloController {

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private RedisStockService redisStockService;

    @GetMapping("/hello")
    public String hello() {
        return "🔥 秒杀系统已启动！";
    }

    @GetMapping("/products")
    public List<Product> getProducts() {
        return productMapper.selectList(null);
    }

    @GetMapping("/seckill")
    public String seckill(@RequestParam Integer productId, @RequestParam Integer quantity) {
        // 1. 从 Redis 原子扣减库存
        Long remain = redisStockService.decrStock(productId, quantity);
        if (remain == null || remain == -2) {
            return "商品不存在";
        }
        if (remain == -1) {
            return "库存不足，剩余：" + redisStockService.getStock(productId);
        }

        // 2. 扣减成功，同步更新 MySQL 库存（后续可改为异步）
        Product product = productMapper.selectById(productId);
        if (product == null) {
            return "商品不存在";
        }
        product.setStock(remain.intValue());
        productMapper.updateById(product);

        // 3. 生成订单
        Order order = new Order();
        order.setProductId(productId);
        order.setQuantity(quantity);
        order.setTotalPrice(product.getPrice().multiply(new BigDecimal(quantity)));
        order.setUserId("test_user");
        orderMapper.insert(order);

        return "秒杀成功！剩余库存：" + remain + "，订单ID：" + order.getId();
    }
}