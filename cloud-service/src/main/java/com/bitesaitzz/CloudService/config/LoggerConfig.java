package com.bitesaitzz.CloudService.config;

import org.springframework.context.annotation.Bean;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.slf4j.Logger;
@Configuration
public class LoggerConfig {

    @Bean
    public Logger logger(){
        return LoggerFactory.getLogger("GlobalLogger");
    }
}
