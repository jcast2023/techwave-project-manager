package com.cibertec.entity; // Manteniendo el paquete 'cibertec'

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode; // Importar esta anotación
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set; // Si tienes colecciones OneToMany

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
    private String username;

    @Column(name = "contrasena", nullable = false, length = 255)
    private String password;

    @Column(name = "nombre", nullable = false, length = 100)
    private String firstName;

    @Column(name = "apellido", nullable = false, length = 100)
    private String lastName;

    @Column(name = "email", nullable = false, unique = true, length = 100)
    private String email;

    @CreationTimestamp
    @Column(name = "fecha_creacion", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "activo", nullable = false)
    private Boolean active = true;

    @ManyToOne(fetch = FetchType.EAGER) // Un usuario tiene un rol
    @JoinColumn(name = "rol_id", nullable = false) // Columna FK en la tabla usuarios
    @EqualsAndHashCode.Exclude // Excluir para evitar ciclos con Role (si Role tiene Set<User>)
    private Role role;

    // Si tienes relaciones OneToMany en User, también DEBEN ser excluidas:
    // @OneToMany(mappedBy = "assignedTo", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    // @EqualsAndHashCode.Exclude
    // private Set<Task> tasksAssigned = new HashSet<>();

    // @OneToMany(mappedBy = "projectManager", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    // @EqualsAndHashCode.Exclude
    // private Set<Project> projectsManaged = new HashSet<>();

    // @OneToMany(mappedBy = "uploadedBy", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    // @EqualsAndHashCode.Exclude
    // private Set<Attachment> uploadedAttachments = new HashSet<>();
}
