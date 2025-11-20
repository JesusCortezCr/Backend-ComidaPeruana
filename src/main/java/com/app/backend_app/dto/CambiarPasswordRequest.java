package com.app.backend_app.dto;

import lombok.Data;

@Data
public class CambiarPasswordRequest {

    private String passwordActual;
    private String passwordNuevo;
}
