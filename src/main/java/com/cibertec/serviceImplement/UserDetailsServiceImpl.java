package com.cibertec.serviceImplement;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.cibertec.entity.User;
import com.cibertec.repository.UserRepository;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
	
	@Autowired
	private UserRepository usuarioRepository;
	
	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		
		User usuario = usuarioRepository.findOneByEmail(email)
				.orElseThrow( () -> new UsernameNotFoundException("El usuario buscado con email "+email+",no se encuentra registrado"));
						
		return new UserDetailImplement(usuario);
	}
	
}
