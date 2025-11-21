package com.app.backend_app.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.app.backend_app.models.Especialidad;
@Repository
public interface EspecialidadRepository extends JpaRepository<Especialidad, Long>{

    List<Especialidad> findByActivaTrueOrderByNombreAsc();
    Optional<Especialidad> findById(Long id);
    Optional<Especialidad> findByNombre(String nombre);
}
