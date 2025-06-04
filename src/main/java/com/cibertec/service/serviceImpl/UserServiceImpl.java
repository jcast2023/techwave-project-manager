package com.cibertec.service.serviceImpl;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.cibertec.dto.UserDTO;
import com.cibertec.entity.Role;
import com.cibertec.entity.User;
import com.cibertec.exception.ResourceNotFoundException;
import com.cibertec.repository.RoleRepository;
import com.cibertec.repository.UserRepository;
import com.cibertec.service.UserService;

import jakarta.transaction.Transactional;

@Service
public class UserServiceImpl implements UserService{
	
	private final UserRepository userRepository;
    private final RoleRepository roleRepository; // Inyectamos el RoleRepository
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder; // Inyectamos PasswordEncoder para seguridad

    // Inyección de dependencias por constructor (preferida)
    public UserServiceImpl(UserRepository userRepository, RoleRepository roleRepository, ModelMapper modelMapper, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.modelMapper = modelMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional // Asegura que el método se ejecute dentro de una transacción de base de datos
    public UserDTO createUser(UserDTO userDTO) {
        // Antes de crear, verifica si el nombre de usuario o email ya existen
        if (userRepository.existsByUsername(userDTO.getUsername())) {
            throw new IllegalArgumentException("El nombre de usuario '" + userDTO.getUsername() + "' ya está en uso.");
        }
        if (userRepository.existsByEmail(userDTO.getEmail())) {
            throw new IllegalArgumentException("El email '" + userDTO.getEmail() + "' ya está en uso.");
        }

        User user = modelMapper.map(userDTO, User.class);

        // Encriptar la contraseña antes de guardarla
        user.setPassword(passwordEncoder.encode(userDTO.getPassword())); // Asumiendo que UserDTO tiene un getPassword()

        // Asignar el rol al usuario
        // Asumimos que userDTO.getRole() tiene un ID o nombre de rol válido
        if (userDTO.getRole() != null && userDTO.getRole().getId() != null) {
            Role role = roleRepository.findById(userDTO.getRole().getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Role", "id", userDTO.getRole().getId()));
            user.setRole(role);
        } else if (userDTO.getRole() != null && userDTO.getRole().getNombre() != null) {
            Role role = roleRepository.findByNombre(userDTO.getRole().getNombre())
                    .orElseThrow(() -> new ResourceNotFoundException("Role", "nombre", userDTO.getRole().getNombre()));
            user.setRole(role);
        } else {
            // Si no se proporciona un rol, puedes asignar un rol por defecto o lanzar una excepción
            throw new IllegalArgumentException("El rol del usuario es obligatorio.");
        }

        User savedUser = userRepository.save(user);
        return modelMapper.map(savedUser, UserDTO.class);
    }

    @Override
    public UserDTO getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
        return modelMapper.map(user, UserDTO.class);
    }

    @Override
    public List<UserDTO> getAllUsers() {
        List<User> users = userRepository.findAll();
        return users.stream()
                .map(user -> modelMapper.map(user, UserDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public UserDTO updateUser(Long id, UserDTO userDTO) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));

        // Actualizar campos (puedes añadir lógica para evitar actualizar campos si son nulos en DTO)
        existingUser.setFirstName(userDTO.getFirstName());
        existingUser.setLastName(userDTO.getLastName());
        existingUser.setEmail(userDTO.getEmail());
        existingUser.setActive(userDTO.getActive());

        // Si se proporciona una nueva contraseña, encriptarla
        if (userDTO.getPassword() != null && !userDTO.getPassword().isEmpty()) {
            existingUser.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        }

        // Actualizar el rol si se proporciona
        if (userDTO.getRole() != null && userDTO.getRole().getId() != null) {
            Role role = roleRepository.findById(userDTO.getRole().getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Role", "id", userDTO.getRole().getId()));
            existingUser.setRole(role);
        } else if (userDTO.getRole() != null && userDTO.getRole().getNombre() != null) {
             Role role = roleRepository.findByNombre(userDTO.getRole().getNombre())
                    .orElseThrow(() -> new ResourceNotFoundException("Role", "nombre", userDTO.getRole().getNombre()));
            existingUser.setRole(role);
        }


        User updatedUser = userRepository.save(existingUser);
        return modelMapper.map(updatedUser, UserDTO.class);
    }

    @Override
    @Transactional
    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
        userRepository.delete(user);
    }

    @Override
    public Optional<UserDTO> findByUsername(String username) {
        return userRepository.findByUsername(username)
                .map(user -> modelMapper.map(user, UserDTO.class));
    }

    @Override
    public Optional<UserDTO> findByEmail(String email) {
        return userRepository.findByEmail(email)
                .map(user -> modelMapper.map(user, UserDTO.class));
    }
}
