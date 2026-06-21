# seckill-system 秒杀系统
基于 Spring Boot 3 + Redis + MySQL 实现的简易秒杀后端

## 技术栈
- JDK 21
- Spring Boot 3.4.3
- MySQL 8.0
- Redis（Docker容器部署）
- MyBatis-Plus 3.5.9

## 项目功能
1. Redis原子扣减库存，解决并发超卖问题
2. 秒杀下单自动生成订单记录
3. Redis缓存与MySQL数据库数据同步

## 本地运行步骤
1. 启动MySQL 8.0，创建业务库并执行建表SQL
2. 使用Docker运行Redis容器
3. 修改 `application.properties` 配置数据库、Redis连接地址
4. 运行 `DemoApplication.java` 启动项目
5. 调用秒杀接口完成测试