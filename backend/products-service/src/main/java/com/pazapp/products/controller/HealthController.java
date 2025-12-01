package com.pazapp.products.controller;

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
        response.put("service", "products-service");
        response.put("port", 8081);
        response.put("endpoints", Map.of(
            "GET /productos", "Lista todos los productos",
            "GET /productos/{id}", "Obtiene un producto por ID",
            "GET /productos/buscar?nombre={nombre}", "Busca productos por nombre",
            "POST /productos", "Crea un nuevo producto",
            "PUT /productos/{id}", "Actualiza un producto",
            "DELETE /productos/{id}", "Elimina un producto"
        ));
        return ResponseEntity.ok(response);
    }
}

