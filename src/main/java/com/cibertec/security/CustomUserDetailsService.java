package com.cibertec.security;

import com.cibertec.entity.User;
import com.cibertec.repository.UserRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Collections; // Para Collections.singletonList

/**
 * Implementación personalizada de UserDetailsService para cargar los detalles del usuario.
 * Utiliza UserRepository para buscar usuarios en la base de datos.
 */
@Service // Marca esta clase como un componente de servicio de Spring
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String usernameOrEmail) throws UsernameNotFoundException {
        // Carga el usuario por nombre de usuario o email desde la base de datos
        User user = userRepository.findByUsername(usernameOrEmail)
                .orElseGet(() -> userRepository.findByEmail(usernameOrEmail)
                        .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado con nombre de usuario o email: " + usernameOrEmail)));

        // Construye un objeto UserDetails de Spring Security
        return new org.springframework.security.core.userdetails.User(user.getUsername(),
                user.getPassword(), mapRolesToAuthorities(user.getRole().getNombre()));
    }

    // Método auxiliar para mapear el nombre del rol a una colección de GrantedAuthority
    private Collection<? extends GrantedAuthority> mapRolesToAuthorities(String roleName) {
        // Spring Security espera roles con el prefijo "ROLE_"
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + roleName));
    }
}
