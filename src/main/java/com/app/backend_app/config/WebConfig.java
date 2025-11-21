package com.app.backend_app.config;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import lombok.extern.slf4j.Slf4j;

@Slf4j  // 👈 AGREGAR ESTO
@Configuration
public class WebConfig implements WebMvcConfigurer {
    
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        Path uploadsDir = Paths.get("uploads").toAbsolutePath().normalize();
        String uploadsPath = "file:" + uploadsDir.toString() + "/";
        
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations(uploadsPath);
        
        log.info("🔧 Configurando servidor de archivos estáticos:"); // 👈 LOG
        log.info("   - Ruta: /uploads/**"); // 👈 LOG
        log.info("   - Directorio: {}", uploadsPath); // 👈 LOG
    }
}