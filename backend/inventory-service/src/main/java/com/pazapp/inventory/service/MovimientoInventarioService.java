package com.pazapp.inventory.service;

import com.pazapp.inventory.model.MovimientoInventario;
import com.pazapp.inventory.repository.MovimientoInventarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MovimientoInventarioService {
    
    @Autowired
    private MovimientoInventarioRepository movimientoRepository;
    
    public List<MovimientoInventario> getAllMovimientos() {
        return movimientoRepository.findAll();
    }
    
    public MovimientoInventario createMovimiento(MovimientoInventario movimiento) {
        return movimientoRepository.save(movimiento);
    }
    
    public List<MovimientoInventario> getMovimientosByProductoId(Long productoId) {
        return movimientoRepository.findByProductoIdOrderByFechaDesc(productoId);
    }
    
    public List<MovimientoInventario> getMovimientosByTipo(String tipo) {
        return movimientoRepository.findByTipoOrderByFechaDesc(tipo);
    }
}

