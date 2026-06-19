package com.sivebo.ms_finanzas;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient
@SpringBootApplication
public class MsFinanzasApplication {

    public static void main(String[] args) {
        SpringApplication.run(MsFinanzasApplication.class, args);
    }

}
