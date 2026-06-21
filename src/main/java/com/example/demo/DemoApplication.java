package com.example.demo;

import com.example.demo.entity.Product;
import com.example.demo.mapper.ProductMapper;
import com.example.demo.service.RedisStockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.List;

@SpringBootApplication
public class DemoApplication implements CommandLineRunner {

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private RedisStockService redisStockService;

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        List<Product> products = productMapper.selectList(null);
        for (Product p : products) {
            redisStockService.initStock(p.getId(), p.getStock());
            System.out.println("🔥 预热库存：商品ID=" + p.getId() + "，库存=" + p.getStock());
        }
    }
}