package com.cibertec.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProjectDTO {
	
	private Long id;
    private String name;
    private String description;
    private LocalDate startDate;
    private LocalDate expectedEndDate;
    private String status;
    private BigDecimal budget;
    private UserDTO projectManager; // DTO anidado para el gerente de proyecto
    private LocalDateTime createdAt;
    private LocalDateTime lastUpdated;

}
