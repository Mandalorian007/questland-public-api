package com.questland.handbook.config;

import org.springframework.boot.convert.ApplicationConversionService;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class AppWebMvcConfigurer implements WebMvcConfigurer {

  @Override
  public void addFormatters(FormatterRegistry registry) {
    // This allows case insensitive enums in binding for path variables and params
    ApplicationConversionService.configure(registry);
  }
}
