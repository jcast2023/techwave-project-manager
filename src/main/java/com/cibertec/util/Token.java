package com.cibertec.util;

import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List; // Necesario para List
import java.util.Map;
import java.util.stream.Collectors; // Necesario para Collectors

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority; // Necesario para GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority; // Necesario para SimpleGrantedAuthority

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;


public class Token {
	
	private final static String TOKEN_FIRMA = "aLg3eqbV254pZd9AFiMh4mAcRAt1Y0Jb";//32 caracteres
	private final static Long TOKEN_DURACION = 3_600L;//TIEMPO DE DURACIÓN (en segundos)
	
	// Modificación: Ahora acepta una colección de GrantedAuthority para incluir los roles
	public static String crearToken(String user, String email, Collection<? extends GrantedAuthority> authorities) {
		
		long expiracionTiempo = TOKEN_DURACION * 1_000; // El tiempo asignado deberá estar en milisegundos
		Date expiracionFecha = new Date(System.currentTimeMillis() + expiracionTiempo);
		
		Map<String, Object> claims = new HashMap<>();
		claims.put("nombre", user); // Puedes mantener este claim si lo usas
		
		// Convertir las GrantedAuthority a una lista de Strings para incluirlas en el token
		List<String> roles = authorities.stream()
										.map(GrantedAuthority::getAuthority)
										.collect(Collectors.toList());
		
		claims.put("authorities", roles); // <-- ¡Añadir los roles como un claim!
		
		return Jwts.builder()
				.setSubject(email)
				.setExpiration(expiracionFecha)
				.addClaims(claims) // Usar addClaims para añadir el mapa
				.signWith(Keys.hmacShaKeyFor(TOKEN_FIRMA.getBytes()))
				.compact();
	}
	
	public static UsernamePasswordAuthenticationToken getAuth(String token) {
		
		try {
			Claims claims = Jwts.parserBuilder()
					.setSigningKey(TOKEN_FIRMA.getBytes())
					.build()
					.parseClaimsJws(token)
					.getBody();
			
			String email = claims.getSubject();
			
			// Extraer las autoridades (roles) del claim "authorities"
			// Jsonwebtoken guarda las listas como List<String> por defecto.
			List<String> rolesFromToken = (List<String>) claims.get("authorities"); 
			
			// Convertir la lista de Strings a List<GrantedAuthority>
			List<GrantedAuthority> authorities = rolesFromToken != null ? 
					rolesFromToken.stream()
							.map(SimpleGrantedAuthority::new) // Convertir cada String a SimpleGrantedAuthority
							.collect(Collectors.toList()) : 
					Collections.emptyList(); // Si no hay roles, devuelve una lista vacía
					
			return new UsernamePasswordAuthenticationToken(email, null, authorities); // <-- ¡Pasar las autoridades!
					
		} catch (Exception e) {
			System.out.println("Sucedio un error al comprobar el token: " + e.getMessage());
			// Es crucial devolver null o lanzar una excepción si el token es inválido
			// para que JWTAuthorizationFilter pueda manejarlo.
			return null;
		}
	}
}