package com.cibertec.entity; // Manteniendo el paquete 'cibertec'

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode; // Importar esta anotación
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
    private String contentType;

    @Column(name = "ruta_almacenamiento", nullable = false, length = 255)
    private String storagePath;

    @Column(name = "tamano_bytes")
    private Long sizeBytes;

    @CreationTimestamp
    @Column(name = "fecha_subida", updatable = false)
    private LocalDateTime uploadedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_subida_id", nullable = false)
    @EqualsAndHashCode.Exclude // Excluir para evitar ciclos con User (si User tiene Set<Attachment>)
    private User uploadedBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tarea_id")
    @EqualsAndHashCode.Exclude // Excluir para evitar ciclos con Task (si Task tiene Set<Attachment>)
    private Task task;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "proyecto_id")
    @EqualsAndHashCode.Exclude // Excluir para evitar ciclos con Project (si Project tiene Set<Attachment>)
    private Project project;
}
