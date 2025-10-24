package com.app.backend_app.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/token")
@RestController
public class TokenController {

    @GetMapping("/verificar")
    public String mostrarToken(){
        return "Ya estas autenticado";
    }

}
