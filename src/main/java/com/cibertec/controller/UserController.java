package com.cibertec.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
// import org.springframework.web.bind.annotation.RequestParam; // No usado en los métodos actuales
import org.springframework.web.bind.annotation.RestController;

import com.cibertec.dto.UserDTO;
// import com.cibertec.entity.User; // No es necesario importar la entidad User aquí, el controlador trabaja con DTOs
import com.cibertec.exception.ResourceNotFoundException;
import com.cibertec.service.UserService;
// import com.cibertec.serviceImplement.UserDetailsServiceImpl; // No es necesario inyectar este servicio aquí, es para Spring Security

import jakarta.validation.Valid; // Necesario para @Valid

// Importar Logger
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class); // Instancia del logger

	// @Autowired // Se prefiere la inyección por constructor para dependencias finales
	// private UserDetailsServiceImpl service; // ESTO DEBE ELIMINARSE. UserDetailsServiceImpl es para Spring Security, no para CRUD de usuarios.
	
	private final UserService userService; // Correcto: inyección de dependencia del servicio de usuarios

    // Constructor para inyección de dependencia de UserService
    // @Autowired es opcional aquí desde Spring 4.3 si solo hay un constructor
    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Obtiene todos los usuarios.
     * GET /api/users
     * @return ResponseEntity con una lista de UserDTOs y estado HTTP 200 (OK) o 204 (No Content) si no hay usuarios.
     */
    @GetMapping
    public ResponseEntity<List<UserDTO>> getAllUsers() { // Renombrado a un nombre más claro
        logger.info("--- [UserController] INICIO: Solicitud para obtener todos los usuarios.");
        try {
            List<UserDTO> users = userService.getAllUsers();

            if(users.isEmpty()) {
                logger.info("--- [UserController] No se encontraron usuarios.");
                return ResponseEntity.noContent().build(); // HTTP 204 No Content
            } else {
                logger.info("--- [UserController] Se encontraron {} usuarios.", users.size());
                return ResponseEntity.ok(users); // HTTP 200 OK
            }

        } catch (Exception e) {
            logger.error("--- [UserController] Error al obtener todos los usuarios: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build(); // HTTP 500 Internal Server Error
        }
    }

    /**
     * Crea un nuevo usuario.
     * POST /api/users
     * @param userDTO El DTO del usuario a crear.
     * @return ResponseEntity con el UserDTO creado y estado HTTP 201 (Created).
     */
    @PostMapping
    public ResponseEntity<UserDTO> createUser(@Valid @RequestBody UserDTO userDTO) {
        logger.info("--- [UserController] INICIO: Solicitud para crear usuario con email: {}", userDTO.getEmail());
        try {
            UserDTO createdUser = userService.createUser(userDTO);
            logger.info("--- [UserController] Usuario creado con éxito. ID: {}", createdUser.getId());
            return new ResponseEntity<>(createdUser, HttpStatus.CREATED); // HTTP 201 Created
        } catch (IllegalArgumentException e) { // Captura si el email ya existe, por ejemplo
            logger.warn("--- [UserController] Error al crear usuario: {}. Causa: {}", e.getMessage(), e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build(); // HTTP 400 Bad Request
        } catch (Exception e) {
            logger.error("--- [UserController] Error inesperado al crear usuario con email {}: {}", userDTO.getEmail(), e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build(); // HTTP 500 Internal Server Error
        }
    }

    /**
     * Obtiene un usuario por su ID.
     * GET /api/users/{id}
     * @param id El ID del usuario.
     * @return ResponseEntity con el UserDTO y estado HTTP 200 (OK) o 404 (Not Found).
     */
    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long id) {
        logger.info("--- [UserController] INICIO: Solicitud para obtener usuario por ID: {}", id);
        try {
            UserDTO user = userService.getUserById(id);
            logger.info("--- [UserController] Usuario encontrado con ID: {}", id);
            return ResponseEntity.ok(user); // HTTP 200 OK
        } catch (ResourceNotFoundException e) {
            logger.warn("--- [UserController] Usuario no encontrado con ID: {}. Causa: {}", id, e.getMessage());
            return ResponseEntity.notFound().build(); // HTTP 404 Not Found
        } catch (Exception e) {
            logger.error("--- [UserController] Error inesperado al obtener usuario con ID {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build(); // HTTP 500 Internal Server Error
        }
    }

    /**
     * Actualiza un usuario existente.
     * PUT /api/users/{id}
     * @param id El ID del usuario a actualizar.
     * @param userDTO El DTO del usuario con la información actualizada.
     * @return ResponseEntity con el UserDTO actualizado y estado HTTP 200 (OK) o 404 (Not Found).
     */
    @PutMapping("/{id}")
    public ResponseEntity<UserDTO> updateUser(@PathVariable Long id, @Valid @RequestBody UserDTO userDTO) {
        logger.info("--- [UserController] INICIO: Solicitud para actualizar usuario con ID: {}", id);
        try {
            UserDTO updatedUser = userService.updateUser(id, userDTO);
            logger.info("--- [UserController] Usuario actualizado con éxito. ID: {}", updatedUser.getId());
            return ResponseEntity.ok(updatedUser); // HTTP 200 OK
        } catch (ResourceNotFoundException e) {
            logger.warn("--- [UserController] Usuario a actualizar no encontrado con ID: {}. Causa: {}", id, e.getMessage());
            return ResponseEntity.notFound().build(); // HTTP 404 Not Found
        } catch (IllegalArgumentException e) { // Por ejemplo, si el email actualizado ya existe
            logger.warn("--- [UserController] Error al actualizar usuario con ID {}: {}. Causa: {}", id, e.getMessage(), e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build(); // HTTP 400 Bad Request
        } catch (Exception e) {
            logger.error("--- [UserController] Error inesperado al actualizar usuario con ID {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build(); // HTTP 500 Internal Server Error
        }
    }

    /**
     * Elimina un usuario por su ID.
     * DELETE /api/users/{id}
     * @param id El ID del usuario a eliminar.
     * @return ResponseEntity con estado HTTP 204 (No Content) o 404 (Not Found).
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        logger.info("--- [UserController] INICIO: Solicitud para eliminar usuario por ID: {}", id);
        try {
            userService.deleteUser(id);
            logger.info("--- [UserController] Usuario eliminado con éxito. ID: {}", id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT); // HTTP 204 No Content
        } catch (ResourceNotFoundException e) {
            logger.warn("--- [UserController] Usuario a eliminar no encontrado con ID: {}. Causa: {}", id, e.getMessage());
            return ResponseEntity.notFound().build(); // HTTP 404 Not Found
        } catch (Exception e) {
            logger.error("--- [UserController] Error inesperado al eliminar usuario con ID {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build(); // HTTP 500 Internal Server Error
        }
    }
}