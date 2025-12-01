package com.pazapp.inventory.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pazapp.inventory.model.MovimientoInventario;
import com.pazapp.inventory.repository.MovimientoInventarioRepository;

@Service
public class MovimientoInventarioService {

    @Autowired
    private MovimientoInventarioRepository repository;

    public List<MovimientoInventario> findAll() {
        return repository.findAll();
    }

    public List<MovimientoInventario> findByProductoId(Long productoId) {
        return repository.findByProductoId(productoId);
    }

    @SuppressWarnings("null")
    public MovimientoInventario save(MovimientoInventario movimiento) {
        return repository.save(movimiento);
    }
}
