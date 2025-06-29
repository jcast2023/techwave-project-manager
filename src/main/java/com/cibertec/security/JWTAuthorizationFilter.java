package com.cibertec.security;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.cibertec.util.Token; // Tu clase Token

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

// Importar Logger para un mejor manejo de errores (opcional pero recomendado)
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class JWTAuthorizationFilter extends OncePerRequestFilter{

    private static final Logger logger = LoggerFactory.getLogger(JWTAuthorizationFilter.class); // Añadir logger

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		
		// 1. Obtener la cabecera "Authorization"
		String bearerToken = request.getHeader("Authorization");
		
		// 2. Verificar si la cabecera existe y tiene el formato "Bearer "
		if( bearerToken != null && bearerToken.startsWith("Bearer ")) {
			// 3. Extraer el token JWT real
			String token = bearerToken.replace("Bearer ", "");
			
			// 4. Validar el token y obtener el objeto de autenticación
            // Token.getAuth ahora se encarga de extraer los roles
            UsernamePasswordAuthenticationToken userPat = null;
            try {
                userPat = Token.getAuth(token);
            } catch (Exception e) {
                // Capturar cualquier excepción de parseo de token y loggearla
                logger.error("Error al procesar el token JWT en JWTAuthorizationFilter: {}", e.getMessage());
                // Opcional: limpiar el contexto de seguridad si el token es inválido
                SecurityContextHolder.clearContext(); 
                // Considera enviar un error 401 aquí si no quieres que la petición siga
                // response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token inválido");
                filterChain.doFilter(request, response); // Continúa con la cadena incluso si hay error
                return; // Detener el procesamiento del filtro si hay un error
            }

            // 5. Si el token es válido y se obtuvo un objeto de autenticación, establecerlo en el contexto de seguridad
            if (userPat != null) {
                SecurityContextHolder.getContext().setAuthentication(userPat);
                logger.debug("SecurityContextHolder establecido con autenticación para usuario: {}", userPat.getName());
                logger.debug("Autoridades cargadas: {}", userPat.getAuthorities());
            } else {
                logger.warn("Token válido pero no se pudo obtener objeto de autenticación para la petición: {}", request.getRequestURI());
            }
		} else {
            // Si no hay token o no tiene el formato esperado, limpiar el contexto de seguridad
            // para asegurar que no haya autenticaciones residuales de peticiones anteriores.
            SecurityContextHolder.clearContext();
        }
		
		// 6. Continuar con la cadena de filtros
		filterChain.doFilter(request, response);
	}
}