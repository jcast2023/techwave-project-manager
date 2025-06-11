package com.cibertec.config;

import com.cibertec.security.JwtAuthEntryPoint;
import com.cibertec.security.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
 // Importar si vas a usar HttpMethod.GET etc.
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity; // ¡IMPORTANTE para @PreAuthorize!
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy; // Para configurar la política de sesión
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter; // Para añadir el filtro JWT

/**
 * Configuración de seguridad principal para la aplicación.
 * Define la cadena de filtros de seguridad, el manejador de autenticación,
 * y las reglas de autorización para los endpoints, integrando JWT.
 */
@Configuration
@EnableWebSecurity // Habilita la configuración de seguridad web de Spring
@EnableMethodSecurity // ¡Habilita la seguridad a nivel de método (ej. @PreAuthorize)!
public class SecurityConfig {

    private final JwtAuthEntryPoint authenticationEntryPoint;
    private final JwtAuthenticationFilter authenticationFilter;

    // Inyectamos el JwtAuthEntryPoint y JwtAuthenticationFilter
    // Spring los encontrará como @Component
    public SecurityConfig(JwtAuthEntryPoint authenticationEntryPoint, JwtAuthenticationFilter authenticationFilter) {
        this.authenticationEntryPoint = authenticationEntryPoint;
        this.authenticationFilter = authenticationFilter;
    }

    // Define el PasswordEncoder que se usará para encriptar contraseñas
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Bean para el AuthenticationManager
    // Es necesario para la autenticación de usuarios en el AuthController
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    // Configura la cadena de filtros de seguridad HTTP
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable()) // Deshabilita CSRF para APIs REST sin estado (comunicación basada en tokens)
            .exceptionHandling(exception -> exception
                .authenticationEntryPoint(authenticationEntryPoint)) // Manejador personalizado para errores de autenticación (401)
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // Configura la política de sesión sin estado (cada solicitud con token, no hay sesiones de usuario en el servidor)
            .authorizeHttpRequests(authorize -> authorize
                // Permite el acceso público al endpoint de autenticación (login)
                .requestMatchers("/api/auth/**").permitAll()
                // Opcional: Permitir acceso público a la documentación de Swagger (si la implementas en el futuro)
                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-resources/**", "/swagger-resources").permitAll()
                // Todas las demás solicitudes requieren autenticación (token JWT válido)
                .anyRequest().authenticated()
                // Si quieres permitir acceso a algunos GETs públicos, podrías usar:
                // .requestMatchers(HttpMethod.GET, "/api/projects").permitAll()
                // .requestMatchers(HttpMethod.GET, "/api/users").permitAll() // Ten cuidado con lo que expones
            );

        // Añade el filtro JWT personalizado ANTES del filtro de autenticación estándar de Spring Security
        // Esto asegura que el JWT sea validado antes de que Spring intente autenticar con usuario/contraseña
        http.addFilterBefore(authenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
