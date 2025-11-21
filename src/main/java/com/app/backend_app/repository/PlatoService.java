package com.app.backend_app.repository;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.app.backend_app.dto.CategoriaDTO;
import com.app.backend_app.dto.CrearPlatoRequest;
import com.app.backend_app.dto.EspecialidadDTO;
import com.app.backend_app.dto.PlatoDTO;
import com.app.backend_app.models.Categoria;
import com.app.backend_app.models.Especialidad;
import com.app.backend_app.models.Plato;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PlatoService {

    private final PlatoRepository platoRepository;
    private final CategoriaRepository categoriaRepository;
    private final EspecialidadRepository especialidadRepository;
    private final FavoritoRepository favoritoRepository;

    /**
     * Obtener platos con filtros
     */
    @Transactional(readOnly = true)
    public List<PlatoDTO> obtenerPlatosConFiltros(
        Long idCategoria, 
        Long idEspecialidad, 
        String rangoPrecio,
        Long idUsuario
    ) {
        Plato.RangoPrecio rango = null;
        
        if (rangoPrecio != null && !rangoPrecio.isEmpty()) {
            try {
                rango = Plato.RangoPrecio.valueOf(rangoPrecio.toUpperCase());
            } catch (IllegalArgumentException e) {
                // Si el rango no es válido, ignorar el filtro
            }
        }

        List<Plato> platos = platoRepository.buscarConFiltros(idCategoria, idEspecialidad, rango);

        return platos.stream()
            .map(plato -> convertirAPlatoDTO(plato, idUsuario))
            .collect(Collectors.toList());
    }

    /**
     * Obtener todas las categorías activas
     */
    @Transactional(readOnly = true)
    public List<CategoriaDTO> obtenerCategorias() {
        return categoriaRepository.findByActivaTrueOrderByOrdenAsc()
            .stream()
            .map(this::convertirACategoriaDTO)
            .collect(Collectors.toList());
    }

    /**
     * Obtener todas las especialidades activas
     */
    @Transactional(readOnly = true)
    public List<EspecialidadDTO> obtenerEspecialidades() {
        return especialidadRepository.findByActivaTrueOrderByNombreAsc()
            .stream()
            .map(this::convertirAEspecialidadDTO)
            .collect(Collectors.toList());
    }

    /**
     * Obtener platos destacados
     */
    @Transactional(readOnly = true)
    public List<PlatoDTO> obtenerPlatosDestacados(Long idUsuario) {
        return platoRepository.findByEsDestacadoTrueAndDisponibleTrue()
            .stream()
            .map(plato -> convertirAPlatoDTO(plato, idUsuario))
            .collect(Collectors.toList());
    }

    /**
     * Convertir Plato a PlatoDTO
     */
    private PlatoDTO convertirAPlatoDTO(Plato plato, Long idUsuario) {
        boolean esFavorito = false;
        
        if (idUsuario != null) {
            esFavorito = favoritoRepository.existsByUsuario_IdUsuarioAndPlato_IdPlato(
                idUsuario, plato.getIdPlato()
            );
        }

        return PlatoDTO.builder()
            .idPlato(plato.getIdPlato())
            .nombre(plato.getNombre())
            .descripcion(plato.getDescripcion())
            .precio(plato.getPrecio())
            .imagenUrl(plato.getImagenUrl())
            .disponible(plato.getDisponible())
            .esDestacado(plato.getEsDestacado())
            .tiempoPreparacion(plato.getTiempoPreparacion())
            .rangoPrecio(plato.getRangoPrecio().name())
            .idCategoria(plato.getCategoria().getIdCategoria())
            .nombreCategoria(plato.getCategoria().getNombre())
            .idEspecialidad(plato.getEspecialidad().getIdEspecialidad())
            .nombreEspecialidad(plato.getEspecialidad().getNombre())
            .esFavorito(esFavorito)
            .build();
    }

    /**
     * Convertir Categoria a CategoriaDTO
     */
    private CategoriaDTO convertirACategoriaDTO(Categoria categoria) {
        return CategoriaDTO.builder()
            .idCategoria(categoria.getIdCategoria())
            .nombre(categoria.getNombre())
            .descripcion(categoria.getDescripcion())
            .imagenUrl(categoria.getImagenUrl())
            .build();
    }

    /**
     * Convertir Especialidad a EspecialidadDTO
     */
    private EspecialidadDTO convertirAEspecialidadDTO(Especialidad especialidad) {
        return EspecialidadDTO.builder()
            .idEspecialidad(especialidad.getIdEspecialidad())
            .nombre(especialidad.getNombre())
            .descripcion(especialidad.getDescripcion())
            .imagenUrl(especialidad.getImagenUrl())
            .build();
    }


    @Transactional
    public PlatoDTO crearPlato(CrearPlatoRequest request) {
        // Validar categoría
        Categoria categoria = categoriaRepository.findById(request.getIdCategoria())
            .orElseThrow(() -> new RuntimeException("Categoría no encontrada"));

        // Validar especialidad
        Especialidad especialidad = especialidadRepository.findById(request.getIdEspecialidad())
            .orElseThrow(() -> new RuntimeException("Especialidad no encontrada"));

        // Validar rango de precio
        Plato.RangoPrecio rangoPrecio;
        try {
            rangoPrecio = Plato.RangoPrecio.valueOf(request.getRangoPrecio().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Rango de precio inválido. Debe ser: ECONOMICO, MEDIO o PREMIUM");
        }

        // Crear el plato
        Plato plato = Plato.builder()
            .nombre(request.getNombre())
            .descripcion(request.getDescripcion())
            .precio(request.getPrecio())
            .categoria(categoria)
            .especialidad(especialidad)
            .imagenUrl(request.getImagenUrl())
            .disponible(request.getDisponible() != null ? request.getDisponible() : true)
            .esDestacado(request.getEsDestacado() != null ? request.getEsDestacado() : false)
            .tiempoPreparacion(request.getTiempoPreparacion())
            .rangoPrecio(rangoPrecio)
            .build();

        Plato platoGuardado = platoRepository.save(plato);
        return convertirAPlatoDTO(platoGuardado, null);
    }

    /**
     * ADMIN - Actualizar un plato existente
     */
    @Transactional
    public PlatoDTO actualizarPlato(Long idPlato, CrearPlatoRequest request) {
        Plato plato = platoRepository.findById(idPlato)
            .orElseThrow(() -> new RuntimeException("Plato no encontrado"));

        // Actualizar categoría si cambió
        if (!plato.getCategoria().getIdCategoria().equals(request.getIdCategoria())) {
            Categoria categoria = categoriaRepository.findById(request.getIdCategoria())
                .orElseThrow(() -> new RuntimeException("Categoría no encontrada"));
            plato.setCategoria(categoria);
        }

        // Actualizar especialidad si cambió
        if (!plato.getEspecialidad().getIdEspecialidad().equals(request.getIdEspecialidad())) {
            Especialidad especialidad = especialidadRepository.findById(request.getIdEspecialidad())
                .orElseThrow(() -> new RuntimeException("Especialidad no encontrada"));
            plato.setEspecialidad(especialidad);
        }

        // Validar y actualizar rango de precio
        Plato.RangoPrecio rangoPrecio;
        try {
            rangoPrecio = Plato.RangoPrecio.valueOf(request.getRangoPrecio().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Rango de precio inválido");
        }

        // Actualizar campos
        plato.setNombre(request.getNombre());
        plato.setDescripcion(request.getDescripcion());
        plato.setPrecio(request.getPrecio());
        plato.setImagenUrl(request.getImagenUrl());
        plato.setDisponible(request.getDisponible());
        plato.setEsDestacado(request.getEsDestacado());
        plato.setTiempoPreparacion(request.getTiempoPreparacion());
        plato.setRangoPrecio(rangoPrecio);

        Plato platoActualizado = platoRepository.save(plato);
        return convertirAPlatoDTO(platoActualizado, null);
    }

    /**
     * ADMIN - Eliminar un plato
     */
    @Transactional
    public void eliminarPlato(Long idPlato) {
        Plato plato = platoRepository.findById(idPlato)
            .orElseThrow(() -> new RuntimeException("Plato no encontrado"));
        
        platoRepository.delete(plato);
    }

    /**
     * ADMIN - Obtener un plato por ID
     */
    @Transactional(readOnly = true)
    public PlatoDTO obtenerPlatoPorId(Long idPlato) {
        Plato plato = platoRepository.findById(idPlato)
            .orElseThrow(() -> new RuntimeException("Plato no encontrado"));
        return convertirAPlatoDTO(plato, null);
    }

    /**
     * ADMIN - Obtener todos los platos (incluyendo no disponibles)
     */
    @Transactional(readOnly = true)
    public List<PlatoDTO> obtenerTodosLosPlatos() {
        return platoRepository.findAll()
            .stream()
            .map(plato -> convertirAPlatoDTO(plato, null))
            .collect(Collectors.toList());
    }
}