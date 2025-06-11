package com.cibertec.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import jakarta.validation.constraints.NotBlank; // Para validación, si no tienes esta dependencia en pom.xml, añádela.

/**
 * DTO para la solicitud de inicio de sesión.
 * Contiene el nombre de usuario/email y la contraseña.
 */
@Data // Genera getters, setters, toString, equals, hashCode
@NoArgsConstructor // Genera constructor sin argumentos
@AllArgsConstructor // Genera constructor con todos los argumentos
public class LoginRequestDTO {
    @NotBlank(message = "El nombre de usuario o email no puede estar vacío")
    private String usernameOrEmail;

    @NotBlank(message = "La contraseña no puede estar vacía")
    private String password;
}

