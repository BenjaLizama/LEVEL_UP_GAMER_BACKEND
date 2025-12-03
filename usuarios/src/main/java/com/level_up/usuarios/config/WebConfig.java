package com.level_up.usuarios.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${file.upload-dir}")
    private String uploadDir;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String path = uploadDir.endsWith("/") ? uploadDir : uploadDir + "/";

        String location = "file:" + path;

        registry.addResourceHandler("/uploads/**")
                .addResourceLocations(location);

        System.out.println("📂 CARPETA DE PROYECTO DETECTADA: " + location);
    }
}