package com.example.vwalkblog.controller.interceptor;


import com.example.vwalkblog.common.JacksonObjectMapper;
import com.example.vwalkblog.controller.interceptor.interc.LoginInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

import java.util.List;

/*
*   配置各种拦截器
* */
@Configuration
@Slf4j
public class LoginCheckSupport extends WebMvcConfigurationSupport {

    @Value("${vwalkblog.cover-path}")
    private String basePath;
    @Value("${vwalkblog.image-path}")
    private String imagePath;
    @Autowired
    private LoginInterceptor loginInterceptor;

    @Override
    protected void addInterceptors(InterceptorRegistry registry) {
        log.info("开启拦截器");
        registry.addInterceptor(loginInterceptor).addPathPatterns("/**")
                .excludePathPatterns("/user/login",
                        "/user/logout",
                        "/user/sign_in_pwd",
                        "/user/sign_up_pwd",
                        "/backend/**",
                        "/front/**",
                        "/user/sendMsg",
                        "/user/login",
                        basePath,
                        imagePath,
                        // Swagger文件
                        "/webjars/**",
                        "/doc.html",
                        "/swagger-resources",
                        "/v2/api-docs");
    }
//    拦截静态资源
    @Override
    protected void addResourceHandlers(ResourceHandlerRegistry registry) {
        log.info("开始进行静态资源映射");
        registry.addResourceHandler("/backend/**").addResourceLocations("classpath:/backend/");
        registry.addResourceHandler("/front/**").addResourceLocations("classpath:/front/");
        registry.addResourceHandler("/webjars/**").addResourceLocations("classpath:/META-INF/resources/webjars/");
        registry.addResourceHandler("doc.html").addResourceLocations("classpath:/META-INF/resources/");
    }

    /*
    *   扩展MVC框架消息转换器，(MVC默认底层有一个消息转换器，这里相当于替换掉默认的)
    *   和 config.JacksonObjectMapper 一起使用
    * */

    @Override
    protected void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        // 创建一个新的消息转换器
        MappingJackson2HttpMessageConverter messageConverter = new MappingJackson2HttpMessageConverter();
        // 设置对象转换器，底层使用Jackson将java对象转为json
        messageConverter.setObjectMapper(new JacksonObjectMapper());
        // 将上面的消息转换器对象追加到mvc框架的转换器集合中
        converters.add(0,messageConverter);
    }
}
