package com.northstarbank.console.config;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.*;
@Configuration public class ApiCorsConfiguration implements WebMvcConfigurer{
 @Override public void addCorsMappings(CorsRegistry registry){registry.addMapping("/api/customers/**").allowedOriginPatterns("*").allowCredentials(true).allowedMethods("*").allowedHeaders("*");}
}
