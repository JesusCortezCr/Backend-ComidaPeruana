package com.app.backend_app.security.filter;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import com.app.backend_app.security.service.JwtService;
import io.micrometer.common.lang.NonNull;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

//filtro que intercepta cada peticion HTTP para validar el token JWT
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter  extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        
        // 1. Extraer el header Authorization
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String userEmail;

        // 2. Validar que el header existe y empieza con "Bearer "
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // 3. Extraer el token (quitar "Bearer " del inicio)
        jwt = authHeader.substring(7);
        
        try {
            // 4. Extraer el email del token
            userEmail = jwtService.extractUsername(jwt);

            // 5. Validar que el email existe y que no hay autenticación previa
            if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                
                // 6. Cargar los detalles del usuario desde la base de datos
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);

                // 7. Validar que el token es válido
                if (jwtService.isTokenValid(jwt, userDetails)) {
                    
                    // 8. Crear el objeto de autenticación
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );
                    
                    // 9. Agregar detalles de la petición
                    authToken.setDetails(
                            new WebAuthenticationDetailsSource().buildDetails(request)
                    );
                    
                    // 10. Establecer la autenticación en el contexto de seguridad
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
        } catch (Exception e) {
            // Si hay algún error al procesar el token, continuar sin autenticar
            logger.error("Error al procesar JWT: " + e.getMessage());
        }

        // 11. Continuar con la cadena de filtros
        filterChain.doFilter(request, response);
    }
}
