package com.level_up.usuarios.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.File;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    private final String RUTA_LOCAL_IMAGENES = "uploads/";
    private final String URL_PUBLIC_IMAGENES = "profile-images/**";

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String rutaAbsoluta = new File(RUTA_LOCAL_IMAGENES).getAbsolutePath();

        String handlerPath = "file:/" + rutaAbsoluta.replace(File.separator, "/") + "/";

        registry.addResourceHandler(URL_PUBLIC_IMAGENES)
                .addResourceLocations(handlerPath);

        System.out.println("CONFIGURACION DE IMAGENES: Mapeando URL " + URL_PUBLIC_IMAGENES + " a ruta local: " + handlerPath);
    }
}
