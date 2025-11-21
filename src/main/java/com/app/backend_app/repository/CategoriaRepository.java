package com.app.backend_app.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.app.backend_app.models.Categoria;
import com.app.backend_app.models.Especialidad;

@Repository
public interface CategoriaRepository extends JpaRepository<Categoria, Long>{

    List<Categoria> findByActivaTrueOrderByOrdenAsc();
    Optional<Categoria> findById(Long id);
Optional<Categoria> findByNombre(String nombre);
}
