package com.pazapp.inventory.service;

import com.pazapp.inventory.model.MovimientoInventario;
import com.pazapp.inventory.repository.MovimientoInventarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

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

    public MovimientoInventario save(MovimientoInventario movimiento) {
        return repository.save(movimiento);
    }
}
