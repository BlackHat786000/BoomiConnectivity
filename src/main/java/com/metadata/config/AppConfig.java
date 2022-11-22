package com.metadata.config;

import com.metadata.rest.dto.BoomiSecret;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.support.BasicAuthenticationInterceptor;
import org.springframework.web.client.RestTemplate;

@Configuration
public class AppConfig {

    //Auth
    @Bean
    public RestTemplate clientLibraryRestTemplate(){

        return new RestTemplate();
    }

}


