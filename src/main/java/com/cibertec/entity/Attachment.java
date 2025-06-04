package com.cibertec.entity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "archivos_adjuntos")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Attachment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nombre_archivo", nullable = false, length = 255)
    private String fileName;

    @Column(name = "tipo_contenido", length = 100)
    private String contentType; // Ej: application/pdf, image/jpeg

    @Column(name = "ruta_almacenamiento", nullable = false, length = 255)
    private String storagePath; // URL de S3 o ruta local

    @Column(name = "tamano_bytes")
    private Long sizeBytes;

    @CreationTimestamp
    @Column(name = "fecha_subida", updatable = false)
    private LocalDateTime uploadedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_subida_id", nullable = false)
    private User uploadedBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tarea_id")
    private Task task; // Puede adjuntarse a una tarea

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "proyecto_id")
    private Project project; // Puede adjuntarse a un proyecto
}
