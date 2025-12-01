package com.pazapp.inventory.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pazapp.inventory.model.MovimientoInventario;
import com.pazapp.inventory.service.MovimientoInventarioService;

import jakarta.validation.Valid;

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
