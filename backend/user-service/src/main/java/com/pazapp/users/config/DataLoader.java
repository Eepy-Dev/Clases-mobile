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
        if (repository.findByUsername("admin@pasteleria.cl").isEmpty()) {
            Usuario admin = new Usuario();
            admin.setUsername("admin@pasteleria.cl");
            admin.setPassword("admin");
            admin.setEmail("admin@pasteleria.cl");
            repository.save(admin);
            System.out.println("User 'admin@pasteleria.cl' created");
        }

        if (repository.findByUsername("vendedor@pasteleria.cl").isEmpty()) {
            Usuario vendedor = new Usuario();
            vendedor.setUsername("vendedor@pasteleria.cl");
            vendedor.setPassword("vendedor");
            vendedor.setEmail("vendedor@pasteleria.cl");
            repository.save(vendedor);
            System.out.println("User 'vendedor@pasteleria.cl' created");
        }
    }
}
