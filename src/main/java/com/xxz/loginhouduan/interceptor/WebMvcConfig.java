//package com.xxz.loginhouduan.interceptor;
//
//import org.springframework.context.annotation.Configuration;
//import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
//import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
//
//import javax.annotation.Resource;
//
//@Configuration
//public class WebMvcConfig implements WebMvcConfigurer {
//
//    @Resource
//    private AuthenticationInterceptor authenticationInterceptor;
//
//    @Override
//    public void addInterceptors(InterceptorRegistry registry) {
//        // 添加拦截器，并设置拦截路径
//        registry.addInterceptor(authenticationInterceptor).addPathPatterns("/Home");
//    }
//}
//
