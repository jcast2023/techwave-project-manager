package com.cibertec.service.serviceImpl;

import org.springframework.transaction.annotation.Transactional;
import com.cibertec.exception.ResourceNotFoundException;
import com.cibertec.repository.UserRepository;
import com.cibertec.service.UserService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.cibertec.dto.UserDTO;
import com.cibertec.entity.User;
import com.cibertec.entity.Role; // ¡IMPORTANTE! Importar la entidad Role
import com.cibertec.repository.RoleRepository; // ¡IMPORTANTE! Importar el RoleRepository

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;


@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository; // AÑADIDO: Declarar RoleRepository

    // MODIFICADO: Constructor para inyectar RoleRepository
    @Autowired
    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository; // AÑADIDO: Asignar RoleRepository
    }

    @Override
    @Transactional
    public UserDTO createUser(UserDTO userDTO) {
        // Validación: Email ya existe (MEJORA AÑADIDA)
        if (userRepository.findOneByEmail(userDTO.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Ya existe un usuario con el email: " + userDTO.getEmail());
        }

        // ¡CLAVE! Buscar el objeto Role completo por su ID
        // Esto resuelve el error "Column 'nombre' cannot be null" indirectamente,
        // al asegurar que todos los campos del User se llenen, incluyendo el Role.
        Role role = roleRepository.findById(userDTO.getRoleId())
                                  .orElseThrow(() -> new ResourceNotFoundException("Role not found with ID: " + userDTO.getRoleId()));


        User user = convertToEntity(userDTO); // Convertir DTO a entidad
        user.setPassword(passwordEncoder.encode(userDTO.getPassword())); // Codificar la contraseña
        user.setId(null); // Asegurar que el ID sea null para que la DB lo genere
        user.setRole(role); // ¡IMPORTANTE! Asignar el objeto Role completo a la entidad User

        User savedUser = userRepository.save(user); // Guardar el usuario
        return convertToDto(savedUser); // Devolver el DTO del usuario guardado
    }

    @Override
    @Transactional(readOnly = true)
    public UserDTO getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + id));
        return convertToDto(user);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public UserDTO updateUser(Long id, UserDTO userDTO) {
        return userRepository.findById(id).map(existingUser -> {
            existingUser.setUsername(userDTO.getUsername());
            existingUser.setEmail(userDTO.getEmail());
            // AÑADIDO: Actualizar firstName y lastName
            existingUser.setFirstName(userDTO.getFirstName());
            existingUser.setLastName(userDTO.getLastName());
            // AÑADIDO: Actualizar active
            if (userDTO.getActive() != null) { // Solo actualizar si se provee en el DTO
                existingUser.setActive(userDTO.getActive());
            }

            if (userDTO.getPassword() != null && !userDTO.getPassword().isEmpty()) {
                existingUser.setPassword(passwordEncoder.encode(userDTO.getPassword()));
            }

            // AÑADIDO: Actualizar el Role si se proporciona un roleId diferente
            if (userDTO.getRoleId() != null &&
                (existingUser.getRole() == null || !existingUser.getRole().getId().equals(userDTO.getRoleId()))) {
                Role newRole = roleRepository.findById(userDTO.getRoleId())
                                             .orElseThrow(() -> new ResourceNotFoundException("Role not found with ID: " + userDTO.getRoleId()));
                existingUser.setRole(newRole);
            }

            User updatedUser = userRepository.save(existingUser);
            return convertToDto(updatedUser);
        }).orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + id));
    }

    @Override
    @Transactional
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("User not found with ID: " + id);
        }
        userRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<UserDTO> findOneByEmail(String email) {
        return userRepository.findOneByEmail(email)
                .map(this::convertToDto);
    }

    private UserDTO convertToDto(User user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setFirstName(user.getFirstName()); // AÑADIDO: Mapear firstName
        dto.setLastName(user.getLastName());   // AÑADIDO: Mapear lastName
        dto.setActive(user.getActive());       // AÑADIDO: Mapear active
        dto.setCreatedAt(user.getCreatedAt()); // AÑADIDO: Mapear createdAt

        // AÑADIDO: Mapear la información del rol
        if (user.getRole() != null) {
            dto.setRoleId(user.getRole().getId());
            dto.setRoleName(user.getRole().getNombre());
        }
        return dto;
    }

    private User convertToEntity(UserDTO userDTO) {
        User user = new User();
        if (userDTO.getId() != null) {
            user.setId(userDTO.getId());
        }
        user.setUsername(userDTO.getUsername());
        user.setEmail(userDTO.getEmail());
        user.setPassword(userDTO.getPassword()); // Se codifica después, en el método de servicio

        // ¡CLAVE! Mapear firstName y lastName del DTO a la entidad
        user.setFirstName(userDTO.getFirstName());
        user.setLastName(userDTO.getLastName());

        // Manejar el campo 'active' del DTO. Si es null en el DTO, establecer a true por defecto
        user.setActive(userDTO.getActive() != null ? userDTO.getActive() : true);

        // El rol NO se mapea aquí directamente desde userDTO.getRole(),
        // sino que se busca y se setea en el método de servicio (createUser/updateUser)
        return user;
    }
}