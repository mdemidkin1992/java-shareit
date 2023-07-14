package ru.practicum.shareit.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:headers.yml")
public class PropertySourcesConfig {
}
