package com.app.backend_app.dto;

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
public class EspecialidadDTO {

    private Long idEspecialidad;
    private String nombre;
    private String descripcion;
    private String imagenUrl;
}
