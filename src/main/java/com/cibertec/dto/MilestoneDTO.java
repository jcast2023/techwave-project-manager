package com.cibertec.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MilestoneDTO {

	private Long id;
    private String name;
    private String description;
    private LocalDate dueDate;
    private Boolean completed;
    private Long projectId; // Solo el ID del proyecto
    private LocalDateTime createdAt;
}
