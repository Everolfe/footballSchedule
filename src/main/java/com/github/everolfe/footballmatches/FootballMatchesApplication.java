package com.github.everolfe.footballmatches;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;


@SpringBootApplication
@EnableAsync
public class FootballMatchesApplication {

    public static void main(String[] args) {
        SpringApplication.run(FootballMatchesApplication.class, args);
    }

}
