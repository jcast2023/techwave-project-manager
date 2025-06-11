package com.cibertec.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

/**
 * Clase de utilidad para generar y validar JSON Web Tokens (JWT).
 */
@Component // Marca esta clase como un componente de Spring
public class JwtTokenProvider {

    // Se inyecta la clave secreta y la duración de la expiración desde application.properties
    @Value("${app.jwt-secret}")
    private String jwtSecret;

    @Value("${app.jwt-expiration-milliseconds}")
    private long jwtExpirationDate;

    // Genera un JWT
    public String generateToken(Authentication authentication) {
        String username = authentication.getName(); // Obtiene el nombre de usuario del objeto de autenticación

        Date currentDate = new Date();
        Date expireDate = new Date(currentDate.getTime() + jwtExpirationDate); // Calcula la fecha de expiración

        // Construye el JWT
        String token = Jwts.builder()
                .setSubject(username) // Sujeto del token (nombre de usuario)
                .setIssuedAt(new Date()) // Fecha de emisión
                .setExpiration(expireDate) // Fecha de expiración
                .signWith(key()) // Firma el token con la clave secreta
                .compact(); // Compacta el token en una cadena
        return token;
    }

    // Obtiene la clave de firma JWT
    private Key key() {
        // Decodifica la clave secreta base64 y la convierte en una clave segura
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
    }

    // Obtiene el nombre de usuario del JWT
    public String getUsername(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key()) // Establece la clave de firma para la validación
                .build()
                .parseClaimsJws(token) // Parsea el token
                .getBody(); // Obtiene el cuerpo (claims) del token
        return claims.getSubject(); // Retorna el sujeto (nombre de usuario)
    }

    // Valida el JWT
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(key()) // Establece la clave de firma para la validación
                    .build()
                    .parse(token); // Intenta parsear el token
            return true; // Si no hay excepción, el token es válido
        } catch (MalformedJwtException malformedJwtException) {
            System.out.println("Token JWT inválido");
            // Puedes usar un logger aquí en lugar de System.out.println
            // logger.error("Token JWT inválido", malformedJwtException);
        } catch (ExpiredJwtException expiredJwtException) {
            System.out.println("Token JWT expirado");
        } catch (UnsupportedJwtException unsupportedJwtException) {
            System.out.println("Token JWT no soportado");
        } catch (IllegalArgumentException illegalArgumentException) {
            System.out.println("Cadena JWT vacía o nula");
        }
        return false; // Si ocurre alguna excepción, el token es inválido
    }
}
