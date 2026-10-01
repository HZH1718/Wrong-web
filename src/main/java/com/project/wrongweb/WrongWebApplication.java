package com.project.wrongweb;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@MapperScan("com.project.wrongweb.mapper")
@EnableFeignClients(basePackages = "com.project.wrongweb.feign")
public class WrongWebApplication {

    public static void main(String[] args) {
        SpringApplication.run(WrongWebApplication.class, args);
    }

}
