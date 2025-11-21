package com.app.backend_app.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.app.backend_app.dto.CrearPlatoRequest;
import com.app.backend_app.dto.PlatoDTO;
import com.app.backend_app.repository.PlatoService;
import com.app.backend_app.security.service.FileStorageService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin/platos")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMINISTRADOR')") // Solo administradores
public class AdminPlatoController {

    private final PlatoService platoService;
    private final FileStorageService fileStorageService;

    /**
     * Obtener todos los platos (incluyendo no disponibles)
     * GET /api/admin/platos
     */
    @GetMapping
    public ResponseEntity<List<PlatoDTO>> obtenerTodosLosPlatos() {
        List<PlatoDTO> platos = platoService.obtenerTodosLosPlatos();
        return ResponseEntity.ok(platos);
    }

    /**
     * Obtener un plato por ID
     * GET /api/admin/platos/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<PlatoDTO> obtenerPlatoPorId(@PathVariable Long id) {
        try {
            PlatoDTO plato = platoService.obtenerPlatoPorId(id);
            return ResponseEntity.ok(plato);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Crear un nuevo plato
     * POST /api/admin/platos
     */
    @PostMapping
    public ResponseEntity<?> crearPlato(@RequestBody CrearPlatoRequest request) {
        try {
            // Validaciones básicas
            if (request.getNombre() == null || request.getNombre().trim().isEmpty()) {
                return ResponseEntity.badRequest().body("El nombre es obligatorio");
            }
            if (request.getPrecio() == null || request.getPrecio().doubleValue() <= 0) {
                return ResponseEntity.badRequest().body("El precio debe ser mayor a 0");
            }
            if (request.getIdCategoria() == null) {
                return ResponseEntity.badRequest().body("Debe seleccionar una categoría");
            }
            if (request.getIdEspecialidad() == null) {
                return ResponseEntity.badRequest().body("Debe seleccionar una especialidad");
            }

            PlatoDTO plato = platoService.crearPlato(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(plato);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Actualizar un plato existente
     * PUT /api/admin/platos/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarPlato(
            @PathVariable Long id,
            @RequestBody CrearPlatoRequest request) {
        try {
            PlatoDTO plato = platoService.actualizarPlato(id, request);
            return ResponseEntity.ok(plato);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Eliminar un plato
     * DELETE /api/admin/platos/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarPlato(@PathVariable Long id) {
        try {
            platoService.eliminarPlato(id);
            return ResponseEntity.ok().body("Plato eliminado correctamente");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/upload-image")
    public ResponseEntity<?> uploadImage(@RequestParam("file") MultipartFile file) {
        try {
            String fileUrl = fileStorageService.storeFile(file);
            return ResponseEntity.ok().body(new UploadResponse(fileUrl));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Clase interna para la respuesta
    private static class UploadResponse {
        private String fileUrl;

        public UploadResponse(String fileUrl) {
            this.fileUrl = fileUrl;
        }

        public String getFileUrl() {
            return fileUrl;
        }

        public void setFileUrl(String fileUrl) {
            this.fileUrl = fileUrl;
        }
    }
}