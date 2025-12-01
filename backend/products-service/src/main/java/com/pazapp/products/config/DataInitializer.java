package com.pazapp.products.config;

import com.pazapp.products.model.Producto;
import com.pazapp.products.repository.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {
    
    @Autowired
    private ProductoRepository productoRepository;
    
    @Override
    public void run(String... args) throws Exception {
        if (productoRepository.count() == 0) {
            productoRepository.save(new Producto(
                "Torta de Chocolate",
                "Deliciosa torta de chocolate con crema batida",
                25000.0,
                10,
                "https://via.placeholder.com/300?text=Torta+Chocolate"
            ));
            productoRepository.save(new Producto(
                "Pastel de Frutillas",
                "Pastel fresco con frutillas y crema",
                22000.0,
                8,
                "https://via.placeholder.com/300?text=Pastel+Frutillas"
            ));
            productoRepository.save(new Producto(
                "Cupcakes Variados",
                "Set de 6 cupcakes con diferentes sabores",
                15000.0,
                15,
                "https://via.placeholder.com/300?text=Cupcakes"
            ));
            productoRepository.save(new Producto(
                "Brownies",
                "Brownies de chocolate con nueces",
                12000.0,
                20,
                "https://via.placeholder.com/300?text=Brownies"
            ));
            productoRepository.save(new Producto(
                "Tiramisú",
                "Postre italiano clásico",
                28000.0,
                6,
                "https://via.placeholder.com/300?text=Tiramisu"
            ));
            System.out.println("5 productos de pastelería iniciales creados");
        }
    }
}

