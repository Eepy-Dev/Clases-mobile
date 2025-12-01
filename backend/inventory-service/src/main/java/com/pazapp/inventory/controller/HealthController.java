package com.pazapp.inventory.controller;

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
        response.put("service", "inventory-service");
        response.put("port", 8082);
        response.put("endpoints", Map.of(
            "GET /productos", "Lista todos los productos del inventario",
            "GET /productos/{id}", "Obtiene un producto del inventario por ID",
            "GET /movimientos", "Lista todos los movimientos de inventario",
            "GET /movimientos/producto/{id}", "Obtiene movimientos de un producto",
            "POST /movimientos", "Registra un nuevo movimiento de inventario"
        ));
        return ResponseEntity.ok(response);
    }
}

