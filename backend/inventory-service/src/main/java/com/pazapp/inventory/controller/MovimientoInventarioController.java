package com.pazapp.inventory.controller;

import com.pazapp.inventory.model.MovimientoInventario;
import com.pazapp.inventory.service.MovimientoInventarioService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/movimientos")
public class MovimientoInventarioController {

    @Autowired
    private MovimientoInventarioService service;

    @GetMapping
    public List<MovimientoInventario> getAll() {
        return service.findAll();
    }

    @GetMapping("/producto/{productoId}")
    public List<MovimientoInventario> getByProductoId(@PathVariable Long productoId) {
        return service.findByProductoId(productoId);
    }

    @PostMapping
    public MovimientoInventario create(@Valid @RequestBody MovimientoInventario movimiento) {
        return service.save(movimiento);
    }
}
