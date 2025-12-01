package com.pazapp.users.config;

import com.pazapp.users.model.Usuario;
import com.pazapp.users.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataLoader implements CommandLineRunner {

    private final UsuarioRepository repository;

    public DataLoader(UsuarioRepository repository) {
        this.repository = repository;
    }

    @Override
    public void run(String... args) throws Exception {
        if (repository.findByUsername("admin").isEmpty()) {
            Usuario admin = new Usuario();
            admin.setUsername("admin");
            admin.setPassword("admin");
            admin.setEmail("admin@example.com");
            repository.save(admin);
            System.out.println("User 'admin' created with password 'admin'");
        }
    }
}
