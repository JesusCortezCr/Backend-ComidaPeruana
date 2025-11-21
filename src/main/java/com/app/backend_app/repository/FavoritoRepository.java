package com.app.backend_app.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.app.backend_app.models.Favorito;

@Repository
public interface FavoritoRepository extends JpaRepository<Favorito, Long>{

    List<Favorito> findByUsuario_IdUsuario(Long idUsuario);

    Optional<Favorito> findByUsuario_IdUsuarioAndPlato_IdPlato(Long idUsuario, Long idPlato);

    boolean existsByUsuario_IdUsuarioAndPlato_IdPlato(Long idUsuario, Long idPlato);
    
    void deleteByUsuario_IdUsuarioAndPlato_IdPlato(Long idUsuario, Long idPlato);
}
