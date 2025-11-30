package com.pazapp.users.controller;

import com.pazapp.users.model.Usuario;
import com.pazapp.users.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService service;

    @GetMapping
    public List<Usuario> getAll() {
        return service.findAll();
    }

    @PostMapping
    public Usuario create(@Valid @RequestBody Usuario usuario) {
        return service.save(usuario);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> credenciales) {
        String username = credenciales.get("username");
        String password = credenciales.get("password");

        if (service.validarCredenciales(username, password)) {
            return ResponseEntity.ok(Map.of("message", "Login exitoso", "username", username));
        } else {
            return ResponseEntity.status(401).body(Map.of("error", "Credenciales inválidas"));
        }
    }
}
