package com.leteatgo;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LeteatgoController {

    @GetMapping("/")
    public String hello() {
        return "Hello, Leteatgo!";
    }
}
