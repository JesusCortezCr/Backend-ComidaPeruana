package com.app.backend_app.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlatoDTO {

    private Long idPlato;
    private String nombre;
    private String descripcion;
    private BigDecimal precio;
    private String imagenUrl;
    private Boolean disponible;
    private Boolean esDestacado;
    private Integer tiempoPreparacion;
    private String rangoPrecio; // "ECONOMICO", "MEDIO", "PREMIUM"
    
    // Datos de la categoría
    private Long idCategoria;
    private String nombreCategoria;
    
    // Datos de la especialidad
    private Long idEspecialidad;
    private String nombreEspecialidad;
    
    // Para usuarios autenticados
    private Boolean esFavorito;
}
