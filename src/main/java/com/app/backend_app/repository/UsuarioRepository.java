package com.app.backend_app.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.app.backend_app.models.Usuario;

//repositorio para buscar usuarios en la BD
public interface UsuarioRepository extends JpaRepository<Usuario,Long>{

    Optional<Usuario> findByEmail(String email);

    boolean existsByEmail(String email);
}
