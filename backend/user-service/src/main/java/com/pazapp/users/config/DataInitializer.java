package com.pazapp.users.config;

import com.pazapp.users.model.Usuario;
import com.pazapp.users.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {
    
    @Autowired
    private UsuarioRepository usuarioRepository;
    
    @Override
    public void run(String... args) throws Exception {
        if (usuarioRepository.count() == 0) {
            usuarioRepository.save(new Usuario("admin", "admin"));
            usuarioRepository.save(new Usuario("usuario1", "usuario1"));
            usuarioRepository.save(new Usuario("usuario2", "usuario2"));
            System.out.println("Usuarios iniciales creados: admin, usuario1, usuario2");
        }
    }
}

