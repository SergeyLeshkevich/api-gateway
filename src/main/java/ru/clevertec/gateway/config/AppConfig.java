package ru.clevertec.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestTemplate;
import ru.clevertec.exceptionhandlerstarter.handler.NewsManagementSystemExceptionHandler;

/**
 * Configuration class for the application.
 * This class defines beans for RestTemplate, RestClient, and a custom exception handler.
 */
@Configuration
public class AppConfig {

    /**
     * Creates a bean for RestTemplate to be used for making REST calls.
     *
     * @return a RestTemplate instance.
     */
    @Bean
    public RestTemplate template() {
        return new RestTemplate();
    }

    /**
     * Creates a bean for RestClient.Builder to be used for building RestClient instances.
     *
     * @return a RestClient.Builder instance.
     */
    @Bean
    public RestClient.Builder restClient() {
        return RestClient.builder();
    }

    /**
     * Creates a bean for NewsManagementSystemExceptionHandler to handle exceptions in the application.
     *
     * @return a NewsManagementSystemExceptionHandler instance.
     */
    @Bean
    public NewsManagementSystemExceptionHandler handler(){
        return new NewsManagementSystemExceptionHandler();
    }
}
