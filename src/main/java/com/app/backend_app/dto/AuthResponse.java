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
//enviar el token al cliente
public class AuthResponse {

    private String token;
    private String tipo="Bearer";
    private Long idUsuario;
    private String nombre;
    private String apellido;
    private String email;
    private String rol;
    
}
