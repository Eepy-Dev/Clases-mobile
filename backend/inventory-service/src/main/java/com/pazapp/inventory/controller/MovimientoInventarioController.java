package com.pazapp.inventory.controller;

import com.pazapp.inventory.model.MovimientoInventario;
import com.pazapp.inventory.service.MovimientoInventarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/movimientos")
public class MovimientoInventarioController {
    
    @Autowired
    private MovimientoInventarioService movimientoService;
    
    @GetMapping
    public List<MovimientoInventario> getAllMovimientos() {
        return movimientoService.getAllMovimientos();
    }
    
    @PostMapping
    public MovimientoInventario createMovimiento(@RequestBody Map<String, Object> movimientoData) {
        MovimientoInventario movimiento = new MovimientoInventario();
        
        if (movimientoData.get("productoId") != null) {
            movimiento.setProductoId(Long.parseLong(movimientoData.get("productoId").toString()));
        }
        if (movimientoData.get("tipo") != null) {
            String tipo = movimientoData.get("tipo").toString();
            // Normalizar tipo: SALIDA_VENTA, SALIDA_MERMA, SALIDA_MOVIMIENTO -> SALIDA
            if (tipo.startsWith("SALIDA_")) {
                tipo = "SALIDA";
            }
            movimiento.setTipo(tipo);
        }
        if (movimientoData.get("cantidad") != null) {
            movimiento.setCantidad(Integer.parseInt(movimientoData.get("cantidad").toString()));
        }
        if (movimientoData.get("stockAnterior") != null) {
            movimiento.setStockAnterior(Integer.parseInt(movimientoData.get("stockAnterior").toString()));
        }
        if (movimientoData.get("stockNuevo") != null) {
            movimiento.setStockNuevo(Integer.parseInt(movimientoData.get("stockNuevo").toString()));
        }
        
        // Si viene fecha como timestamp, se maneja en el modelo
        // Si no viene, se genera automáticamente con @PrePersist
        
        return movimientoService.createMovimiento(movimiento);
    }
    
    @GetMapping("/producto/{id}")
    public List<MovimientoInventario> getMovimientosByProductoId(@PathVariable Long id) {
        return movimientoService.getMovimientosByProductoId(id);
    }
}
