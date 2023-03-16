package com.learning.lili;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Slf4j // Lombok提供，可以使用注解和log变量
@ServletComponentScan
@EnableTransactionManagement // 开启事务控制
@SpringBootApplication // SpringBoot启动类
@EnableCaching // 开启缓存注解功能
public class LiliApplication {
    public static void main(String[] args) {
        SpringApplication.run(LiliApplication.class, args);
        log.info("项目启动成功！");
    }
}
