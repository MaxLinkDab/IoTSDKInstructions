package com.td.api;

import org.apache.commons.io.FileUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;
import tk.mybatis.spring.annotation.MapperScan;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;

@SpringBootApplication
@MapperScan(value = {"com.td.common_service.mapper", "com.td.api.mapper"})
@EnableScheduling
//@Import(value = {KlockAspectHandler.class})
@ComponentScan(
        basePackages = {
                "com.td.util.config", "com.td.common_service", "com.td.api"
        }
)
//@SecurityScheme(name = "javainuseapi", scheme = "basic", type = SecuritySchemeType.HTTP, in = SecuritySchemeIn.HEADER)
public class ApiMain {
    public static void main(String[] args) {
        SpringApplication.run(ApiMain.class, args);
    }
}
