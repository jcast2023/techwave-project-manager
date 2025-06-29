package com.cibertec.entity; // Manteniendo el paquete 'cibertec' según tu última confirmación

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode; // Importar esta anotación
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "proyectos")
@Data // Genera getters, setters, toString, equals, hashCode
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
    private String status = "PENDIENTE";

    @Column(name = "presupuesto", precision = 15, scale = 2)
    private BigDecimal budget = BigDecimal.ZERO;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "gerente_proyecto_id", nullable = false)
    @EqualsAndHashCode.Exclude // Excluir de equals/hashCode para evitar ciclos
    private User projectManager;

    @CreationTimestamp
    @Column(name = "fecha_creacion", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "ultima_actualizacion")
    private LocalDateTime lastUpdated;

    // Relaciones @OneToMany: DEBEN ser excluidas de equals/hashCode
    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @EqualsAndHashCode.Exclude // Excluir para evitar ConcurrentModificationException/ciclos
    private Set<Task> tasks = new HashSet<>();

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @EqualsAndHashCode.Exclude // Excluir para evitar ConcurrentModificationException/ciclos
    private Set<Milestone> milestones = new HashSet<>();

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @EqualsAndHashCode.Exclude // Excluir para evitar ConcurrentModificationException/ciclos
    private Set<Attachment> attachments = new HashSet<>();

}
