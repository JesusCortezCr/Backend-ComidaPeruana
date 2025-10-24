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
//recibir email y password del login
public class LoginRequest {
    
    private String email;
    private String password;
}
