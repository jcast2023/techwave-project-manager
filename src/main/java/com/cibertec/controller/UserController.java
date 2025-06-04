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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.cibertec.dto.UserDTO;
import com.cibertec.service.UserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Crea un nuevo usuario.
     * POST /api/users
     * @param userDTO El DTO del usuario a crear.
     * @return ResponseEntity con el UserDTO creado y estado HTTP 201 (Created).
     */
    @PostMapping
    public ResponseEntity<UserDTO> createUser(@Valid @RequestBody UserDTO userDTO) {
        UserDTO createdUser = userService.createUser(userDTO);
        return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
    }

    /**
     * Obtiene un usuario por su ID.
     * GET /api/users/{id}
     * @param id El ID del usuario.
     * @return ResponseEntity con el UserDTO y estado HTTP 200 (OK).
     */
    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long id) {
        UserDTO user = userService.getUserById(id);
        return ResponseEntity.ok(user);
    }

    /**
     * Obtiene todos los usuarios.
     * GET /api/users
     * @return ResponseEntity con una lista de UserDTOs y estado HTTP 200 (OK).
     */
    @GetMapping
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        List<UserDTO> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    /**
     * Actualiza un usuario existente.
     * PUT /api/users/{id}
     * @param id El ID del usuario a actualizar.
     * @param userDTO El DTO del usuario con la información actualizada.
     * @return ResponseEntity con el UserDTO actualizado y estado HTTP 200 (OK).
     */
    @PutMapping("/{id}")
    public ResponseEntity<UserDTO> updateUser(@PathVariable Long id, @Valid @RequestBody UserDTO userDTO) {
        UserDTO updatedUser = userService.updateUser(id, userDTO);
        return ResponseEntity.ok(updatedUser);
    }

    /**
     * Elimina un usuario por su ID.
     * DELETE /api/users/{id}
     * @param id El ID del usuario a eliminar.
     * @return ResponseEntity con estado HTTP 204 (No Content).
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    /**
     * Busca un usuario por su nombre de usuario.
     * GET /api/users/search/by-username?username=valor
     * @param username El nombre de usuario a buscar.
     * @return ResponseEntity con el UserDTO si se encuentra, o 404 Not Found.
     */
    @GetMapping("/search/by-username")
    public ResponseEntity<UserDTO> getUserByUsername(@RequestParam String username) {
        return userService.findByUsername(username)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Busca un usuario por su dirección de correo electrónico.
     * GET /api/users/search/by-email?email=valor
     * @param email La dirección de correo electrónico a buscar.
     * @return ResponseEntity con el UserDTO si se encuentra, o 404 Not Found.
     */
    @GetMapping("/search/by-email")
    public ResponseEntity<UserDTO> getUserByEmail(@RequestParam String email) {
        return userService.findByEmail(email)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
