package com.talentsync;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories
public class TalentSyncApplication {

    public static void main(String[] args) {
        SpringApplication.run(TalentSyncApplication.class, args);
    }
}
