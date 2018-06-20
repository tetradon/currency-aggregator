package com.aggregator.config;


import com.aggregator.service.CurrencyInMemoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.servlet.ServletContext;
import java.io.File;



@Configuration
@EnableWebMvc
@ComponentScan("com.aggregator")

public class AppConfig implements WebMvcConfigurer {

    @Bean
    @Autowired
    public CurrencyInMemoryService currencyInMemoryService(
            ServletContext servletContext) {
        File folder = new File(servletContext.getRealPath("/WEB-INF/rates/"));
        return new CurrencyInMemoryService(folder);
    }

    @Override
    public final void addResourceHandlers(
            final ResourceHandlerRegistry registry) {
        registry
                .addResourceHandler("/rates/**")
                .addResourceLocations("/rates/");
    }

}









