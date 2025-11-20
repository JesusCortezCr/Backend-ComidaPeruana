package com.app.backend_app.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.app.backend_app.dto.CambiarPasswordRequest;
import com.app.backend_app.models.Usuario;
import com.app.backend_app.repository.UsuarioRepository;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final PasswordEncoder passwordEncoder;

    private final UsuarioRepository usuarioRepository;



    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarDatosPublicos(@RequestBody Usuario usuarioActualizado , @PathVariable Long id, Authentication authentication){
        Usuario usuarioAutenticado= (Usuario)authentication.getPrincipal();
        if(!usuarioAutenticado.getIdUsuario().equals(id)){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        Usuario usuario=usuarioRepository.findById(id).orElseThrow(()-> new RuntimeException("Usuario con el id "+id+" no encontado"));
        usuario.setNombre(usuarioActualizado.getNombre());
        usuario.setApellido(usuarioActualizado.getApellido());
        usuarioRepository.save(usuario);

        return ResponseEntity.ok().body(usuario);
    }


    @PutMapping("/{id}/cambiar-contrasenia")
    public ResponseEntity<?> actualizarContrasenia(@PathVariable Long id , Authentication authentication, @RequestBody CambiarPasswordRequest  request){
        Usuario usuarioAutenticado=(Usuario) authentication.getPrincipal();
        if(!passwordEncoder.matches(request.getPasswordActual(),usuarioAutenticado.getPassword())){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Contraseña incorrecta");
        }
        usuarioAutenticado.setPassword(passwordEncoder.encode(request.getPasswordNuevo()));
        usuarioRepository.save(usuarioAutenticado);
        return ResponseEntity.ok().build();
    }


}
