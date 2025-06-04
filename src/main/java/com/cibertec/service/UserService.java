package com.cibertec.service;

import java.util.List;
import java.util.Optional;

import com.cibertec.dto.UserDTO;

public interface UserService {
	
	UserDTO createUser(UserDTO userDTO);
	UserDTO getUserById(Long id);
	List<UserDTO> getAllUsers();
	UserDTO updateUser(Long id, UserDTO userDTO);
	void deleteUser(Long id);
	Optional<UserDTO> findByUsername(String username);
	Optional<UserDTO> findByEmail(String email);

}
