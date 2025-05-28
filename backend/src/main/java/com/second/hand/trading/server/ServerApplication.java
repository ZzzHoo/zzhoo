package com.second.hand.trading.server;

import com.second.hand.trading.server.utils.OrderTaskHandler;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Spring Boot 应用启动入口
 * 负责启动整个后端服务
 */
@SpringBootApplication()
public class ServerApplication {

    public static void main(String[] args) {
        // 启动Spring Boot应用
        SpringApplication.run(ServerApplication.class, args);
        // 启动订单延迟任务处理线程（如自动取消超时未支付订单）
        OrderTaskHandler.run();
    }

}
