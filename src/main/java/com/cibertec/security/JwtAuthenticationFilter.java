package com.cibertec.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import com.cibertec.security.CustomUserDetailsService;

import java.io.IOException;

/**
 * Filtro de autenticación JWT que se ejecuta una vez por cada solicitud.
 * Intercepta las solicitudes para validar el token JWT y autenticar al usuario.
 */
@Component // Marca esta clase como un componente de Spring
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final CustomUserDetailsService customUserDetailsService;

    public JwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider, CustomUserDetailsService customUserDetailsService) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.customUserDetailsService = customUserDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        // 1. Obtener el token JWT de la cabecera de la solicitud
        String token = getJwtFromRequest(request);

        // 2. Validar el token y autenticar al usuario
        if (StringUtils.hasText(token) && jwtTokenProvider.validateToken(token)) {
            // Obtener el nombre de usuario del token
            String username = jwtTokenProvider.getUsername(token);

            // Cargar los detalles del usuario
            UserDetails userDetails = customUserDetailsService.loadUserByUsername(username);

            // Crear un objeto de autenticación
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                    userDetails, null, userDetails.getAuthorities()
            );
            authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            // Establecer la autenticación en el contexto de seguridad de Spring
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        }

        // Continuar con la cadena de filtros
        filterChain.doFilter(request, response);
    }

    // Método auxiliar para extraer el JWT de la cabecera "Authorization"
    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        // Comprobar si la cabecera "Authorization" no está vacía y comienza con "Bearer "
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7); // Retorna la parte del token después de "Bearer "
        }
        return null;
    }
}

