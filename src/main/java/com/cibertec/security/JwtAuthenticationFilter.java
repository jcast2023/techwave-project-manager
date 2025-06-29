package com.cibertec.security;

import com.cibertec.entity.Auth; // Tu clase Auth (email, password)
import com.cibertec.serviceImplement.UserDetailImplement; // Tu implementación de UserDetails

import com.fasterxml.jackson.databind.ObjectMapper; // Importar ObjectMapper
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority; // Importar GrantedAuthority
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.Collection; // Importar Collection

import static com.cibertec.util.Token.crearToken; // Importar tu método crearToken

public class JWTAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    // Necesitas inyectar un ObjectMapper si no lo tienes ya en el contexto
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request,
                                                HttpServletResponse response) throws AuthenticationException {
        Auth auth = null;
        String email = "";
        String password = "";

        try {
            // Deserializa el cuerpo de la petición a un objeto Auth
            auth = objectMapper.readValue(request.getInputStream(), Auth.class);
            email = auth.getEmail();
            password = auth.getPassword();
        } catch (IOException e) {
            // Manejar errores de lectura del JSON
            throw new RuntimeException("Error al parsear las credenciales de autenticación", e);
        }

        // Crear el token de autenticación de Spring Security
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(email, password, Collections.emptyList());
        
        // Autenticar con el AuthenticationManager
        return getAuthenticationManager().authenticate(authenticationToken);
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request,
                                            HttpServletResponse response,
                                            FilterChain chain,
                                            Authentication authResult) throws IOException, ServletException {
        // Obtener el UserDetails del resultado de autenticación
        UserDetailImplement userDetail = (UserDetailImplement) authResult.getPrincipal(); // Asumiendo que tu Principal es UserDetailImplement

        // Obtener las autoridades del userDetail
        Collection<? extends GrantedAuthority> authorities = userDetail.getAuthorities();

        // Generar el token JWT incluyendo los roles
        String token = crearToken(userDetail.getUser(), userDetail.getUsername(), authorities); // Usar el nuevo método

        // Añadir el token a la cabecera de la respuesta
        response.addHeader("Authorization", "Bearer " + token);
        response.getWriter().flush();

        // Puedes añadir también el token al cuerpo de la respuesta si tu frontend lo espera así
        Map<String, String> body = new HashMap<>();
        body.put("token", token);
        body.put("message", "Login exitoso");
        body.put("username", userDetail.getUsername());
        body.put("roles", authorities.stream().map(GrantedAuthority::getAuthority).collect(Collectors.joining(","))); // Opcional: enviar roles en el cuerpo

        response.setContentType("application/json");
        response.getWriter().write(objectMapper.writeValueAsString(body));
        response.getWriter().flush();
    }
}