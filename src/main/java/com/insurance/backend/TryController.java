package com.insurance.backend;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController("/")
public class TryController {
    @GetMapping("/hello")
    public String greet(){
        return "Hello World";
    }
}
