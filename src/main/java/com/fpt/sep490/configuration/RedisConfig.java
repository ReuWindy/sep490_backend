package com.fpt.sep490.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.util.FileCopyUtils;

import java.io.IOException;

@Configuration
public class RedisConfig {

    @Bean
    public RedisScript<Long> rateLimiterScript() throws IOException {
        ClassPathResource resource = new ClassPathResource("scripts/script.lua");
        String script = new String(FileCopyUtils.copyToByteArray(resource.getInputStream()));
        return RedisScript.of(script, Long.class);
    }
}