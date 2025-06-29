package com.cibertec.entity; // Manteniendo el paquete 'cibertec'

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode; // Importar esta anotación
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "tareas")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nombre", nullable = false, length = 255)
    private String name;

    @Lob
    @Column(name = "descripcion")
    private String description;

    @Column(name = "fecha_limite")
    private LocalDate dueDate;

    @Column(name = "estado", nullable = false, length = 50)
    private String status = "PENDIENTE";

    @Column(name = "prioridad", nullable = false, length = 20)
    private String priority = "MEDIA";

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "proyecto_id", nullable = false)
    @EqualsAndHashCode.Exclude // Excluir para evitar ciclos con Project
    private Project project;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "asignado_a_usuario_id")
    @EqualsAndHashCode.Exclude // Excluir para evitar ciclos con User (si User tiene Set<Task>)
    private User assignedTo;

    @CreationTimestamp
    @Column(name = "fecha_creacion", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "ultima_actualizacion")
    private LocalDateTime lastUpdated;

    @OneToMany(mappedBy = "task", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @EqualsAndHashCode.Exclude // Excluir para evitar ConcurrentModificationException/ciclos
    private Set<Attachment> attachments = new HashSet<>();
}
