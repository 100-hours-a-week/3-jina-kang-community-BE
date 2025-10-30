package com.ktb.ktb_community;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class KtbCommunityApplication {

    public static void main(String[] args) {
        SpringApplication.run(KtbCommunityApplication.class, args);
    }

}
