package com.leteatgo;

import static java.util.TimeZone.*;

import jakarta.annotation.PostConstruct;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class LeteatgoApplication {

    @PostConstruct
    void started() {
        setDefault(getTimeZone("Asia/Seoul"));
    }

    public static void main(String[] args) {
        SpringApplication.run(LeteatgoApplication.class, args);
    }

}
