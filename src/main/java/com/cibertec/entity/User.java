package com.cibertec.entity;


import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "usuarios")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nombre_usuario", nullable = false, unique = true, length = 100)
    private String username; // Mapea a nombre_usuario en la BD

    @Column(name = "contrasena", nullable = false, length = 255)
    private String password; // Mapea a contrasena en la BD

    @Column(name = "nombre", nullable = false, length = 100)
    private String firstName; // Mapea a nombre en la BD

    @Column(name = "apellido", nullable = false, length = 100)
    private String lastName; // Mapea a apellido en la BD

    @Column(name = "email", nullable = false, unique = true, length = 100)
    private String email;

    @CreationTimestamp
    @Column(name = "fecha_creacion", updatable = false)
    private LocalDateTime createdAt; // Mapea a fecha_creacion en la BD

    @Column(name = "activo", nullable = false)
    private Boolean active = true;

    @ManyToOne(fetch = FetchType.EAGER) // Un usuario tiene un rol
    @JoinColumn(name = "rol_id", nullable = false) // Columna FK en la tabla usuarios
    private Role role;
}

