package com.cibertec.controller;

import com.cibertec.dto.JwtAuthResponseDTO; // Importa el DTO de respuesta JWT
import com.cibertec.dto.LoginRequestDTO;    // Importa el DTO de solicitud de login
import com.cibertec.security.JwtTokenProvider; // Importa tu proveedor de tokens JWT
import jakarta.validation.Valid; // Para validación de DTOs

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controlador REST para la autenticación de usuarios.
 * Expone el endpoint para iniciar sesión y obtener un token JWT.
 */
@RestController // Indica que esta clase es un controlador REST
@RequestMapping("/api/auth") // Define la ruta base para todos los endpoints de este controlador (ej. /api/auth/login)
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;

    // Inyección de dependencias por constructor
    // Spring inyectará AuthenticationManager y JwtTokenProvider (que son Beans)
    public AuthController(AuthenticationManager authenticationManager, JwtTokenProvider jwtTokenProvider) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    /**
     * Endpoint para iniciar sesión y obtener un token JWT.
     * Método HTTP: POST
     * URL: /api/auth/login
     *
     * @param loginRequestDTO DTO con el nombre de usuario/email y la contraseña.
     * @return ResponseEntity con el JwtAuthResponseDTO (incluye el token) y estado HTTP 200 (OK).
     */
    @PostMapping("/login")
    public ResponseEntity<JwtAuthResponseDTO> authenticateUser(@Valid @RequestBody LoginRequestDTO loginRequestDTO) {
        // Autentica al usuario usando el AuthenticationManager
        // Este manager invocará a CustomUserDetailsService para cargar al usuario
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken( // Crea un token de autenticación con las credenciales
                        loginRequestDTO.getUsernameOrEmail(), // Nombre de usuario o email proporcionado por el cliente
                        loginRequestDTO.getPassword()        // Contraseña proporcionada por el cliente
                )
        );

        // Establece la autenticación en el contexto de seguridad de Spring
        // Esto es importante para que el usuario autenticado sea reconocido por el resto de la aplicación
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Genera el token JWT para el usuario autenticado
        String token = jwtTokenProvider.generateToken(authentication);

        // Devuelve el token en la respuesta HTTP
        return ResponseEntity.ok(new JwtAuthResponseDTO(token));
    }
}

