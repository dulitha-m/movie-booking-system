package com.pgno98.moviebookingsystem11.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
public class SimpleTestController {
    
    @GetMapping("/hello")
    public String hello() {
        return "Hello! Application is running!";
    }
    
    @GetMapping("/promotions-test")
    public String testPromotions() {
        return "Promotions test endpoint is working!";
    }
}
