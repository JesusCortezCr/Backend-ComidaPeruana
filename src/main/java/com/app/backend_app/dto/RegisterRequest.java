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
//recibir los datos del registro de usuario
public class RegisterRequest {

    private String nombre;
    private String apellido;
    private String email;
    private String password;
    private String telefono;
    
}
