package com.app.backend_app.security.service;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.app.backend_app.dto.AuthResponse;
import com.app.backend_app.dto.LoginRequest;
import com.app.backend_app.dto.RegisterRequest;
import com.app.backend_app.models.Usuario;
import com.app.backend_app.repository.UsuarioRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    /**
     * Registrar un nuevo usuario
     */
    public AuthResponse register(RegisterRequest request) {
        // Validar que el email no exista
        if (usuarioRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("El email ya está registrado");
        }

        // Crear el usuario
        Usuario usuario = Usuario.builder()
                .nombre(request.getNombre())
                .apellido(request.getApellido())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword())) // Hashear la contraseña
                .telefono(request.getTelefono())
                .rol(Usuario.Rol.CLIENTE) // Por defecto es CLIENTE
                .activo(true)
                .build();

        // Guardar en la base de datos
        Usuario usuarioGuardado = usuarioRepository.save(usuario);

        // Generar token JWT
        String token = jwtService.generateToken(usuarioGuardado);

        // Retornar respuesta
        return AuthResponse.builder()
                .token(token)
                .tipo("Bearer")
                .idUsuario(usuarioGuardado.getIdUsuario())
                .nombre(usuarioGuardado.getNombre())
                .apellido(usuarioGuardado.getApellido())
                .email(usuarioGuardado.getEmail())
                .rol(usuarioGuardado.getRol().name())
                .build();
    }

    /**
     * Login de usuario
     */
    public AuthResponse login(LoginRequest request) {
        // Autenticar con Spring Security
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        // Si llegamos aquí, la autenticación fue exitosa
        Usuario usuario = usuarioRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Validar que el usuario esté activo
        if (!usuario.getActivo()) {
            throw new RuntimeException("Usuario inactivo");
        }

        // Generar token JWT
        String token = jwtService.generateToken(usuario);

        // Retornar respuesta
        return AuthResponse.builder()
                .token(token)
                .tipo("Bearer")
                .idUsuario(usuario.getIdUsuario())
                .nombre(usuario.getNombre())
                .apellido(usuario.getApellido())
                .email(usuario.getEmail())
                .rol(usuario.getRol().name())
                .build();
    }
}
