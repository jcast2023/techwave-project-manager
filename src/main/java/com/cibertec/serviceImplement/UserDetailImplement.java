package com.cibertec.serviceImplement;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet; // Necesario para crear un Set de autoridades
import java.util.Set;     // Necesario para el tipo Set

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority; // ¡IMPORTANTE!
import org.springframework.security.core.userdetails.UserDetails;

import com.cibertec.entity.User; // Tu entidad User

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class UserDetailImplement implements UserDetails {
	
	private final User usuario;

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		// Crea un conjunto para almacenar las autoridades
		Set<GrantedAuthority> authorities = new HashSet<>();

		// Verifica si el usuario tiene un rol asignado y si el nombre del rol no es nulo
		if (usuario.getRole() != null && usuario.getRole().getNombre() != null) {
			// Crea una SimpleGrantedAuthority usando el nombre del rol de la base de datos.
			// Según tus imágenes, los roles en la BD ya tienen el prefijo "ROLE_" (ej. "ROLE_ADMIN").
			// Por lo tanto, lo pasamos directamente sin añadir "ROLE_" de nuevo aquí.
			authorities.add(new SimpleGrantedAuthority(usuario.getRole().getNombre()));
		} else {
			// Opcional: Puedes loggear una advertencia si un usuario no tiene un rol,
			// ya que esto podría ser un estado inesperado para tu aplicación.
			// System.out.println("Advertencia: El usuario " + usuario.getEmail() + " no tiene un rol asignado.");
		}

		return authorities; // Devuelve el conjunto de autoridades
	}
	
	@Override
	public String getUsername() {//hace referencia al correo
		return usuario.getEmail();
	}

	@Override
	public String getPassword() {
		return usuario.getPassword();
	}

	public String getUser() {//hace referencia al nombre del usuario o alias
		return usuario.getUsername(); // Asumiendo que tu entidad User tiene un campo 'username' para el nombre/alias
	}
	
	@Override
    public boolean isAccountNonExpired() {
        return true; 
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return usuario.getActive() != null ? usuario.getActive() : false;
    }
}