package com.cibertec.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;



@Entity
@Table(name = "proyectos")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Project {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nombre", nullable = false, length = 255)
    private String name;

    @Lob // Para textos largos
    @Column(name = "descripcion")
    private String description;

    @Column(name = "fecha_inicio", nullable = false)
    private LocalDate startDate;

    @Column(name = "fecha_fin_esperada")
    private LocalDate expectedEndDate;

    @Column(name = "estado", nullable = false, length = 50)
    private String status = "PENDIENTE"; // Ej: PENDIENTE, EN_PROGRESO, COMPLETADO, CANCELADO

    @Column(name = "presupuesto", precision = 15, scale = 2)
    private BigDecimal budget = BigDecimal.ZERO;

    @ManyToOne(fetch = FetchType.LAZY) // Un proyecto tiene un gerente
    @JoinColumn(name = "gerente_proyecto_id", nullable = false) // Columna FK en la tabla proyectos
    private User projectManager;

    @CreationTimestamp
    @Column(name = "fecha_creacion", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "ultima_actualizacion")
    private LocalDateTime lastUpdated;

    // Relaciones (no se mapean directamente en la BD como FKs, pero son relaciones JPA)
    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<Task> tasks = new HashSet<>(); // Ahora 'Task' se referirá a com.cibertec.entity.Task

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<Milestone> milestones = new HashSet<>();

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<Attachment> attachments = new HashSet<>();

}
