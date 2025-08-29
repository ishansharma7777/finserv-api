package com.finserv.demo.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Simple test controller to verify application is running
 */
@RestController
public class TestController {
    
    @GetMapping("/")
    public String home() {
        return "FinServ API is running! Access H2 Console at: http://localhost:8080/h2-console";
    }
    
    @GetMapping("/test")
    public String test() {
        return "Application is working correctly!";
    }
    
    @GetMapping("/info")
    public String info() {
        return """
            FinServ API Information:
            - H2 Console: http://localhost:8080/h2-console
            - JDBC URL: jdbc:h2:mem:testdb
            - Username: sa
            - Password: password
            - Salary Analysis API: http://localhost:8080/api/salary/
            """;
    }
}
