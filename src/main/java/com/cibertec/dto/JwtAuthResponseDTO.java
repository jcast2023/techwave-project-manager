package com.cibertec.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * DTO para la respuesta de autenticación JWT.
 * Contiene el token de acceso y el tipo de token.
 */
@Data
@NoArgsConstructor // Genera constructor sin argumentos
@AllArgsConstructor // Genera constructor con todos los argumentos (accessToken, tokenType)
public class JwtAuthResponseDTO {
    private String accessToken;
    private String tokenType = "Bearer"; // Tipo de token por defecto

    // Constructor adicional para facilitar la creación cuando solo se tiene el token
    public JwtAuthResponseDTO(String accessToken) {
        this.accessToken = accessToken;
        this.tokenType = "Bearer"; // Asigna el valor por defecto explícitamente
    }
}

