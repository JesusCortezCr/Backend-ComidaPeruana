package com.app.backend_app.security.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.app.backend_app.security.filter.JwtAuthenticationFilter;

import lombok.RequiredArgsConstructor;

/**
 * Configuración principal de Spring Security
 * ✅ Define rutas públicas (/api/auth/** - login/register)
✅ Define rutas protegidas (admin, cliente)
✅ Configura JWT como sistema de autenticación (STATELESS)
✅ Configura BCrypt para hashear contraseñas
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final UserDetailsService userDetailsService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(Customizer.withDefaults())
                // Deshabilitar CSRF (no necesario para APIs REST con JWT)
                .csrf(AbstractHttpConfigurer::disable)
                
                // Configuración de autorización de peticiones
                .authorizeHttpRequests(auth -> auth
                        // Rutas públicas (sin autenticación)
                        .requestMatchers(
                                "/api/auth/**",           // Login y registro
                                "/api/platos/**",
                                "/api/platos/categorias",
                                "/api/platos/especialidades",
                                "/api/platos/destacados",
                                "/uploads/**",         // Endpoints públicos
                                "/error"                  // Página de error
                        ).permitAll()
                        
                        // Rutas solo para ADMINISTRADOR
                        .requestMatchers("/api/admin/**").hasRole("ADMINISTRADOR")
                        
                        // Rutas solo para CLIENTE
                        .requestMatchers("/api/cliente/**").hasRole("CLIENTE")                        
                        // Todas las demás rutas requieren autenticación
                        .anyRequest().authenticated()
                )
                
                // Configurar manejo de sesiones (STATELESS para JWT)
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                
                // Configurar proveedor de autenticación
                .authenticationProvider(authenticationProvider())
                
                // Agregar filtro JWT antes del filtro de autenticación por username/password
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * Proveedor de autenticación
     */
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    /**
     * Gestor de autenticación
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    /**
     * Codificador de contraseñas (BCrypt)
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
