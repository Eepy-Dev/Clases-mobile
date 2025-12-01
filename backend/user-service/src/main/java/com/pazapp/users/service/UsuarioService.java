package com.pazapp.users.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pazapp.users.model.Usuario;
import com.pazapp.users.repository.UsuarioRepository;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository repository;

    public List<Usuario> findAll() {
        return repository.findAll();
    }

    public Optional<Usuario> findById(Long id) {
        return repository.findById(java.util.Objects.requireNonNull(id));
    }

    public Optional<Usuario> findByUsername(String username) {
        return repository.findByUsername(username);
    }

    public Usuario save(Usuario usuario) {
        return repository.save(java.util.Objects.requireNonNull(usuario));
    }

    public boolean validarCredenciales(String username, String password) {
        return repository.findByUsername(username)
                .map(u -> u.getPassword().equals(password))
                .orElse(false);
    }
}
