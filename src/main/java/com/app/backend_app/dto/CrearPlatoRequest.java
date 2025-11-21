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
public class CrearPlatoRequest {

    private String nombre;
    private String descripcion;
    private BigDecimal precio;
    private Long idCategoria;
    private Long idEspecialidad;
    private String imagenUrl;
    private Boolean disponible;
    private Boolean esDestacado;
    private Integer tiempoPreparacion;
    private String rangoPrecio; 

}
