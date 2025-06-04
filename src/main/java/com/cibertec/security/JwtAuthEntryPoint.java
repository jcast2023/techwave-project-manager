package com.cibertec.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Clase que maneja los intentos de acceso no autorizados.
 * Implementa AuthenticationEntryPoint para devolver una respuesta 401 Unauthorized.
 */
@Component // Marca esta clase como un componente de Spring
public class JwtAuthEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException, ServletException {
        // Este método se activa cuando un usuario no autenticado intenta acceder a un recurso protegido.
        // Envía una respuesta de error 401 Unauthorized.
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, authException.getMessage());
    }
}
