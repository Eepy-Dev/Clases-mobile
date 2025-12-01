package com.pazapp.users.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/")
public class HealthController {
    
    @GetMapping
    public ResponseEntity<Map<String, Object>> health() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "UP");
        response.put("service", "user-service");
        response.put("port", 8083);
        response.put("endpoints", Map.of(
            "GET /usuarios", "Lista todos los usuarios",
            "POST /usuarios/login", "Autentica un usuario"
        ));
        return ResponseEntity.ok(response);
    }
}

