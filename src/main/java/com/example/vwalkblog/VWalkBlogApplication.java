package com.example.vwalkblog;

import com.example.vwalkblog.controller.interceptor.LoginCheckSupport;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@MapperScan(basePackages = "com.example.vwalkblog.mapper")
@EnableTransactionManagement
@Import(LoginCheckSupport.class)
@EnableCaching
@Slf4j
public class VWalkBlogApplication {

    public static void main(String[] args) {
        SpringApplication.run(VWalkBlogApplication.class, args);
    }

    @Value("${elastic.address}")
    private String elasticPath;
    @Bean
    // 初始化客户端
    public RestHighLevelClient restHighLevelClient(){
        return new RestHighLevelClient(RestClient.builder(
                HttpHost.create(elasticPath)
        ));
    }



}
