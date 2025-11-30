package com.pazapp.products.controller;

import com.pazapp.products.model.Producto;
import com.pazapp.products.service.ProductoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/productos")
public class ProductoController {

    @Autowired
    private ProductoService productoService;

    @GetMapping
    public List<Producto> getAll() {
        return productoService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Producto> getById(@PathVariable Long id) {
        return productoService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Producto create(@Valid @RequestBody Producto producto) {
        return productoService.save(producto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Producto> update(@PathVariable Long id, @Valid @RequestBody Producto productoDetails) {
        return productoService.findById(id)
                .map(producto -> {
                    producto.setNombre(productoDetails.getNombre());
                    producto.setPrecio(productoDetails.getPrecio());
                    producto.setStock(productoDetails.getStock());
                    producto.setImagenUrl(productoDetails.getImagenUrl());
                    return ResponseEntity.ok(productoService.save(producto));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (productoService.findById(id).isPresent()) {
            productoService.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/buscar")
    public List<Producto> buscar(@RequestParam String nombre) {
        return productoService.buscarPorNombre(nombre);
    }

    @PostMapping("/salida")
    public ResponseEntity<?> registrarSalida(@RequestBody Map<String, Object> payload) {
        try {
            Long id = Long.valueOf(payload.get("id").toString());
            Integer cantidad = Integer.valueOf(payload.get("cantidad").toString());
            Producto producto = productoService.registrarSalida(id, cantidad);
            return ResponseEntity.ok(producto);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
