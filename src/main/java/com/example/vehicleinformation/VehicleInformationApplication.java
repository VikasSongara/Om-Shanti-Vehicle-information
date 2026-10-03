package com.example.vehicleinformation;

import com.example.vehicleinformation.config.AdminProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(AdminProperties.class)
public class VehicleInformationApplication {

    public static void main(String[] args) {
        SpringApplication.run(VehicleInformationApplication.class, args);
    }
}
