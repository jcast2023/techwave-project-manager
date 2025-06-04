package com.cibertec.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AttachmentDTO {
	
	private Long id;
    private String fileName;
    private String contentType;
    private String storagePath;
    private Long sizeBytes;
    private LocalDateTime uploadedAt;
    private Long uploadedById; // Solo el ID del usuario que subió el archivo
    private Long taskId; // Solo el ID de la tarea (puede ser nulo)
    private Long projectId; 

}
