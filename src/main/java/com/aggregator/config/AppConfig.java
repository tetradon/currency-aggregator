package com.aggregator.config;


import com.aggregator.service.CurrencyInMemoryService;
import org.flywaydb.core.Flyway;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.servlet.ServletContext;
import javax.sql.DataSource;
import java.io.File;


@Configuration
@EnableWebMvc
@ComponentScan("com.aggregator")

public class AppConfig implements WebMvcConfigurer {

    @Bean
    public CurrencyInMemoryService currencyInMemoryService(
            ServletContext servletContext) {
        File folder = new File(servletContext.getRealPath("/WEB-INF/rates/"));
        return new CurrencyInMemoryService(folder);
    }


    @Bean
    public DataSource dataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("org.h2.Driver");
        dataSource.setUrl("jdbc:h2:file:~/currency_rates");
        dataSource.setUsername("sa");
        dataSource.setPassword("");
        Flyway flyway = new Flyway();
        flyway.setDataSource(dataSource);
        flyway.migrate();
        return dataSource;
    }

    @Override
    public final void addResourceHandlers(
            final ResourceHandlerRegistry registry) {
        registry
                .addResourceHandler("/rates/**")
                .addResourceLocations("/rates/");
    }

}









