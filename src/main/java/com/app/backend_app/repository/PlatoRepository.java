package com.app.backend_app.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.app.backend_app.models.Plato;

@Repository
public interface PlatoRepository extends JpaRepository<Plato, Long>{

    // Query con filtros dinámicos
    @Query("SELECT p FROM Plato p WHERE " +
           "(:idCategoria IS NULL OR p.categoria.idCategoria = :idCategoria) AND " +
           "(:idEspecialidad IS NULL OR p.especialidad.idEspecialidad = :idEspecialidad) AND " +
           "(:rangoPrecio IS NULL OR p.rangoPrecio = :rangoPrecio) AND " +
           "p.disponible = true " +
           "ORDER BY p.esDestacado DESC, p.nombre ASC")
    List<Plato> buscarConFiltros(
        @Param("idCategoria") Long idCategoria,
        @Param("idEspecialidad") Long idEspecialidad,
        @Param("rangoPrecio") Plato.RangoPrecio rangoPrecio
    );

    // Obtener platos destacados
    List<Plato> findByEsDestacadoTrueAndDisponibleTrue();

    // Obtener por categoría
    List<Plato> findByCategoria_IdCategoriaAndDisponibleTrue(Long idCategoria);
}
