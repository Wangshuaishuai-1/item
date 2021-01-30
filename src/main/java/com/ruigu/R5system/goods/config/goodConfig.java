package com.ruigu.R5system.goods.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("ruigu")
public class goodConfig {
    private String pass;

    @Bean
    public void factory(){

        new StringToLocalDateTimeConverter();
        new SnowFlakeIdHelpe();
        return;
    }

}
