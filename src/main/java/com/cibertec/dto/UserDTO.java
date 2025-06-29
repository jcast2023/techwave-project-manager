package com.cibertec.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {

    private Long id;
    
    @NotBlank(message = "El nombre de usuario no puede estar vacío")
    @Size(min = 3, max = 100, message = "El nombre de usuario debe tener entre 3 y 100 caracteres")
    private String username;

    @NotBlank(message = "La contraseña no puede estar vacía") // Puedes usar groups si quieres que sea opcional en PUT
    @Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres")
    private String password;

    @NotBlank(message = "El email no puede estar vacío")
    @Email(message = "El formato del email no es válido")
    @Size(max = 100, message = "El email no puede exceder los 100 caracteres")
    private String email;

    // --- AGREGAR ESTOS CAMPOS ---
    @NotBlank(message = "El nombre no puede estar vacío")
    @Size(max = 100, message = "El nombre no puede exceder los 100 caracteres")
    private String firstName; // Se mapea a 'nombre' en la entidad User

    @NotBlank(message = "El apellido no puede estar vacío")
    @Size(max = 100, message = "El apellido no puede exceder los 100 caracteres")
    private String lastName; // Se mapea a 'apellido' en la entidad User

    private Boolean active; // Ya lo habías considerado, asegúrate de que esté
    private LocalDateTime createdAt; // Ya lo habías considerado, asegúrate de que esté

    private Long roleId;
    private String roleName; // Para el DTO de salida
}