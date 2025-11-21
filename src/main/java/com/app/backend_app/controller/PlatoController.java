package com.app.backend_app.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.app.backend_app.dto.CategoriaDTO;
import com.app.backend_app.dto.EspecialidadDTO;
import com.app.backend_app.dto.PlatoDTO;
import com.app.backend_app.models.Usuario;
import com.app.backend_app.repository.PlatoService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/platos")
@RequiredArgsConstructor
public class PlatoController {

    private final PlatoService platoService;

    /**
     * Obtener platos con filtros opcionales
     * GET /api/platos?idCategoria=1&idEspecialidad=2&rangoPrecio=ECONOMICO
     */
    @GetMapping
    public ResponseEntity<List<PlatoDTO>> obtenerPlatos(
        @RequestParam(required = false) Long idCategoria,
        @RequestParam(required = false) Long idEspecialidad,
        @RequestParam(required = false) String rangoPrecio,
        Authentication authentication
    ) {
        Long idUsuario = null;
        
        // Si está autenticado, obtener su ID
        if (authentication != null && authentication.isAuthenticated()) {
            Usuario usuario = (Usuario) authentication.getPrincipal();
            idUsuario = usuario.getIdUsuario();
        }

        List<PlatoDTO> platos = platoService.obtenerPlatosConFiltros(
            idCategoria, idEspecialidad, rangoPrecio, idUsuario
        );
        
        return ResponseEntity.ok(platos);
    }

    /**
     * Obtener categorías (Tipos de Plato)
     * GET /api/platos/categorias
     */
    @GetMapping("/categorias")
    public ResponseEntity<List<CategoriaDTO>> obtenerCategorias() {
        List<CategoriaDTO> categorias = platoService.obtenerCategorias();
        return ResponseEntity.ok(categorias);
    }

    /**
     * Obtener especialidades
     * GET /api/platos/especialidades
     */
    @GetMapping("/especialidades")
    public ResponseEntity<List<EspecialidadDTO>> obtenerEspecialidades() {
        List<EspecialidadDTO> especialidades = platoService.obtenerEspecialidades();
        return ResponseEntity.ok(especialidades);
    }

    /**
     * Obtener platos destacados
     * GET /api/platos/destacados
     */
    @GetMapping("/destacados")
    public ResponseEntity<List<PlatoDTO>> obtenerDestacados(Authentication authentication) {
        Long idUsuario = null;
        
        if (authentication != null && authentication.isAuthenticated()) {
            Usuario usuario = (Usuario) authentication.getPrincipal();
            idUsuario = usuario.getIdUsuario();
        }

        List<PlatoDTO> platos = platoService.obtenerPlatosDestacados(idUsuario);
        return ResponseEntity.ok(platos);
    }
}