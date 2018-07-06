package com.aggregator.config;


import com.aggregator.dao.CurrencyRatesDaoImpl;
import com.aggregator.service.CurrencyDbService;
import com.aggregator.service.CurrencyInMemoryService;
import com.aggregator.service.CurrencyService;
import org.flywaydb.core.Flyway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.ConfigurableEnvironment;
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
@PropertySource("classpath:application.properties")
public class AppConfig implements WebMvcConfigurer {
    private final ConfigurableEnvironment environment;

    @Autowired
    public AppConfig(ConfigurableEnvironment env) {
        environment = env;
        environment.setActiveProfiles(
                env.getProperty("spring.profiles.active"));
    }

    @Bean
    @Profile("file")
    public CurrencyService currencyInMemoryService(
            ServletContext servletContext) {
        File folder = new File(servletContext.getRealPath("/WEB-INF/rates/"));
        return new CurrencyInMemoryService(folder);
    }

    @Bean
    @Profile("db")
    public CurrencyService currencyDbService() {
        return new CurrencyDbService(
                new CurrencyRatesDaoImpl(dataSource()));
    }

    @Bean
    @Profile("db")
    public DataSource dataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        String driver = environment.getProperty("jdbc.driver");
        if (driver != null) {
            dataSource.setDriverClassName(driver);
        }
        dataSource.setUrl(environment.getProperty("jdbc.url"));
        dataSource.setUsername(environment.getProperty("jdbc.username"));
        dataSource.setPassword(environment.getProperty("jdbc.password"));
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









