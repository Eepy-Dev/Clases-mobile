package com.pazapp.users.service;

import com.pazapp.users.model.Usuario;
import com.pazapp.users.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService {
    
    @Autowired
    private UsuarioRepository usuarioRepository;
    
    public List<Usuario> findAll() {
        return usuarioRepository.findAll();
    }
    
    public Optional<Usuario> findByUsername(String username) {
        return usuarioRepository.findByUsername(username);
    }
    
    public boolean validarCredenciales(String username, String password) {
        return usuarioRepository.findByUsernameAndPassword(username, password).isPresent();
    }
    
    public Usuario crearUsuario(String username, String password) {
        Usuario usuario = new Usuario(username, password);
        return usuarioRepository.save(usuario);
    }
}

