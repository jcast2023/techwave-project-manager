package com.cibertec.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data // Esto generará getProjectManager() y setProjectManager()
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
    private UserDTO projectManager; // <-- ¡ESTE CAMPO DEBE ESTAR AQUÍ!
    private LocalDateTime createdAt;
    private LocalDateTime lastUpdated;

}