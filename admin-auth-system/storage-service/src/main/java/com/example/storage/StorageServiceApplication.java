package com.example.storage;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import java.util.TimeZone;

@SpringBootApplication
public class StorageServiceApplication {

    public static void main(String[] args) {
        // Fix for "invalid value for parameter 'TimeZone': 'Asia/Calcutta'"
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Kolkata"));
        SpringApplication.run(StorageServiceApplication.class, args);
    }

}
