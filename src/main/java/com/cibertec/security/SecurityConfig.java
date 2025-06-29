package com.cibertec.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import lombok.AllArgsConstructor;

@Configuration 
@AllArgsConstructor
public class SecurityConfig {
	
	private final UserDetailsService userDetailsService;
	private final JWTAuthorizationFilter jwtAuthorizationFilter; 

	@Bean
	SecurityFilterChain securityFilterChain(HttpSecurity http, AuthenticationManager authManager) throws Exception{
		
		JWTAuthenticationFilter jwtAuthenticationFilter = new JWTAuthenticationFilter();
		jwtAuthenticationFilter.setAuthenticationManager(authManager);
		jwtAuthenticationFilter.setFilterProcessesUrl("/login");
		
		return http
				.cors()
				.and()
				.csrf().disable()
				.authorizeRequests()//Inicia la configuración de las reglas de autorización
				.anyRequest()//Indica reglas a aplicar a las solicitudes HTTP
				.authenticated()//Especifica que cualquier solicitud debe ser autenticada
				.and()//Permite volver a configurar del objeto HttpSecurity
				.httpBasic()//la aplicación esperará credenciales (nombre de usuario y contraseña)
				.and()
				.sessionManagement()//Inicia la configuración de la gestión de sesiones
				.sessionCreationPolicy(SessionCreationPolicy.STATELESS)//Cada solicitud HTTP debe contener toda la información necesaria para la autenticación
				.and()
				.addFilter(jwtAuthenticationFilter)
				.addFilterBefore(jwtAuthorizationFilter, UsernamePasswordAuthenticationFilter.class)
				.build();//Finaliza la construcción del SecurityFilterChain y lo devuelve
				
		
	}
	
	@Bean
	PasswordEncoder passwordEncoder() {//define cómo se codificarán (hashearán) las contraseñas antes de almacenarse
		return new BCryptPasswordEncoder();
	}
	/*
	@Bean
	UserDetailsService userDetailsService() {//carga los detalles del usuario
		InMemoryUserDetailsManager memoryManager = new InMemoryUserDetailsManager();//almacena los detalles del usuario en la memoria de la aplicación
		memoryManager.createUser(//crea un usuario de prueba
				User
				.withUsername("eddie")//nombre de usuario 
				.password(passwordEncoder().encode("eddie"))//La contraseña es "eddie", pero se pasa a través del passwordEncoder() (bcrypt) para que se hashee antes de ser almacenada.
				.roles()//asigna roles
				.build()
				);
		
		return memoryManager;
	}
	*/
	@Bean
	AuthenticationManager authManager(HttpSecurity http) throws Exception {//orquesta el proceso de autenticación
		return http
				.getSharedObject(AuthenticationManagerBuilder.class)//Obtiene una instancia de AuthenticationManagerBuilder para configurar el AuthenticationManager
				.userDetailsService(userDetailsService)// Le dice al AuthenticationManager que utilice el userDetailsService() que definiste (el que carga al usuario "eddie" de la memoria)
				.passwordEncoder(passwordEncoder())//Le dice al AuthenticationManager que utilice el passwordEncoder() que definiste para comparar la contraseña
				.and()
				.build()
				;
	}
	
	/*public static void main(String[] args) {
		System.out.println("Contraseña encriptada: " + new BCryptPasswordEncoder().encode("mari"));
	
	*/
}
