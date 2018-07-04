package integration;

import com.aggregator.service.CurrencyInMemoryService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.File;
import java.util.Objects;

@Configuration
@EnableWebMvc
@ComponentScan("com.aggregator")
public class TestAppConfig implements WebMvcConfigurer {

    @Bean
    public CurrencyInMemoryService currencyInMemoryService() {
        ClassLoader classLoader = getClass().getClassLoader();
        File folder = new File(Objects.requireNonNull(classLoader.getResource("rates")).getFile());
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

