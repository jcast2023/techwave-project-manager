package com.cibertec.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity // Habilita la configuración de seguridad web de Spring
public class SecurityConfig {

    // Define el PasswordEncoder que se usará para encriptar contraseñas
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Configura la cadena de filtros de seguridad HTTP
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable()) // Deshabilita CSRF (Cross-Site Request Forgery) para facilitar las pruebas con Postman.
                                          // ¡Habilitar en producción y usar tokens CSRF!
            .authorizeHttpRequests(authorize -> authorize
                .anyRequest().permitAll() // Permite todas las solicitudes sin autenticación.
                                          // ¡CAMBIAR ESTO EN PRODUCCIÓN para proteger tus endpoints!
            );
        return http.build();
    }
}
