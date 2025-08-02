package com.example.universityportal.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.*;

import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        Path uploadDir = Paths.get("photos");
        String absolutePath = uploadDir.toFile().getAbsolutePath();

        registry.addResourceHandler("/students/photo/**")
                .addResourceLocations("file:" + absolutePath + "/");
    }


}
