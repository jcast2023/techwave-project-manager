package com.cibertec.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskDTO {
	
	private Long id;
    private String name;
    private String description;
    private LocalDate dueDate;
    private String status;
    private String priority;
    private Long projectId; // Solo el ID del proyecto para evitar ciclos
    private Long assignedToId; // Solo el ID del usuario asignado
    private LocalDateTime createdAt;
    private LocalDateTime lastUpdated;

}
