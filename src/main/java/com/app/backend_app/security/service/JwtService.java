package com.app.backend_app.security.service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {

    // clave secreta para firmar el jwt
    // se introduce en variables de entorno
    @Value("${jwt.secret.key:404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970}")
    private String secretKey;

    // tiempo de expiracion de 24horas en ms
    @Value("${jwt.expiration:86400000}")
    private long jwtExpiration;

    // extraccion del email(username) del jwt
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    // extraccion del claim del token
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    // generacion del token jwt para el usuario
    public String generateToken(UserDetails userDetails) {
        return generateToken(new HashMap<>(), userDetails);
    }

    /**
     * Genera un token JWT con claims adicionales
     */
    public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        return buildToken(extraClaims, userDetails, jwtExpiration);
    }

    /**
     * Construye el token JWT
     */
    private String buildToken(
            Map<String, Object> extraClaims,
            UserDetails userDetails,
            long expiration) {
        return Jwts
                .builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Valida si el token es válido para el usuario
     */
    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    /**
     * Verifica si el token está expirado
     */
    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    /**
     * Extrae la fecha de expiración del token
     */
    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * Extrae todos los claims del token
     */
    private Claims extractAllClaims(String token) {
        return Jwts
                .parser() // Cambiar de parserBuilder() a parser()
                .verifyWith(getSignInKey()) // Cambiar de setSigningKey() a verifyWith()
                .build()
                .parseSignedClaims(token) // Cambiar de parseClaimsJws() a parseSignedClaims()
                .getPayload(); // Cambiar de getBody() a getPayload()
    }

    /**
     * Obtiene la clave de firma
     */
    private SecretKey getSignInKey() { // Cambiar Key por SecretKey
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

}
