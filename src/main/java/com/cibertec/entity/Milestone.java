package com.cibertec.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "hitos")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Milestone {
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

    @Column(name = "completado", nullable = false) // <-- ¡Mantiene 'completado' como Boolean!
    private Boolean completed; // Valor por defecto se puede establecer en constructor o directamente si es necesario

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "proyecto_id", nullable = false) // Tu DB dice nullable=false para proyecto_id
    @EqualsAndHashCode.Exclude
    private Project project;

    @CreationTimestamp
    @Column(name = "fecha_creacion", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "ultima_actualizacion") // Coincide con tu tabla
    private LocalDateTime lastUpdated;

    // Constructor para inicializar 'completed' a false por defecto si no lo haces en DB
    public Milestone(String name, String description, LocalDate dueDate, Project project) {
        this.name = name;
        this.description = description;
        this.dueDate = dueDate;
        this.project = project;
        this.completed = false; // Valor por defecto para nuevos hitos
    }
}