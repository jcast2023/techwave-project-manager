package com.cibertec.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam; // Ya está importado, pero lo mantengo
import org.springframework.web.bind.annotation.RestController;

import com.cibertec.dto.ProjectDTO; // Asegúrate de que este ProjectDTO tiene 'Long managerId'
import com.cibertec.service.ProjectService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/projects")
public class ProjectController {

    private final ProjectService projectService;

    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    /**
     * Crea un nuevo proyecto.
     * Solo accesible por usuarios con los roles 'ADMIN' o 'PROJECT_MANAGER'.
     * POST /api/projects
     * @param projectDTO El DTO del proyecto a crear.
     * @return ResponseEntity con el ProjectDTO creado y estado HTTP 201 (Created).
     */
    @PreAuthorize("hasAnyRole('ADMIN', 'PROJECT_MANAGER')")
    @PostMapping
    public ResponseEntity<ProjectDTO> createProject(@Valid @RequestBody ProjectDTO projectDTO) {
        ProjectDTO createdProject = projectService.createProject(projectDTO);
        return new ResponseEntity<>(createdProject, HttpStatus.CREATED);
    }

    /**
     * Obtiene un proyecto por su ID.
     * Accesible por cualquier usuario autenticado.
     * GET /api/projects/{id}
     * @param id El ID del proyecto.
     * @return ResponseEntity con el ProjectDTO y estado HTTP 200 (OK).
     */
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{id}")
    public ResponseEntity<ProjectDTO> getProjectById(@PathVariable Long id) {
        ProjectDTO project = projectService.getProjectById(id);
        return ResponseEntity.ok(project);
    }

    /**
     * Obtiene todos los proyectos.
     * Accesible por cualquier usuario autenticado.
     * GET /api/projects
     * @return ResponseEntity con una lista de ProjectDTOs y estado HTTP 200 (OK).
     */
    @PreAuthorize("isAuthenticated()")
    @GetMapping
    public ResponseEntity<List<ProjectDTO>> getAllProjects() {
        List<ProjectDTO> projects = projectService.getAllProjects();
        // Opcional: Si no hay proyectos, devolver 204 No Content en lugar de 200 OK con lista vacía
        if (projects.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(projects);
    }

    /**
     * Actualiza un proyecto existente.
     * Solo accesible por usuarios con el rol 'ADMIN', o por el 'PROJECT_MANAGER' asignado a ese proyecto.
     * PUT /api/projects/{id}
     * @param id El ID del proyecto a actualizar.
     * @param projectDTO El DTO del proyecto con la información actualizada.
     * @return ResponseEntity con el ProjectDTO actualizado y estado HTTP 200 (OK).
     */
    @PreAuthorize("hasRole('ADMIN') or @projectService.isProjectManager(#id, authentication.name)")
    @PutMapping("/{id}")
    public ResponseEntity<ProjectDTO> updateProject(@PathVariable Long id, @Valid @RequestBody ProjectDTO projectDTO) {
        ProjectDTO updatedProject = projectService.updateProject(id, projectDTO);
        return ResponseEntity.ok(updatedProject);
    }

    /**
     * Elimina un proyecto por su ID.
     * Solo accesible por usuarios con el rol 'ADMIN'.
     * DELETE /api/projects/{id}
     * @param id El ID del proyecto a eliminar.
     * @return ResponseEntity con estado HTTP 204 (No Content).
     */
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProject(@PathVariable Long id) {
        projectService.deleteProject(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    // --- Métodos de Búsqueda Adicionales (Descomentados e implementados) ---

    /**
     * Busca proyectos por nombre (parcial, ignorando mayúsculas/minúsculas).
     * GET /api/projects/search/by-name?name=valor
     * @param name El nombre parcial del proyecto.
     * @return Lista de ProjectDTOs que coinciden.
     */
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/search/by-name")
    public ResponseEntity<List<ProjectDTO>> searchProjectsByName(@RequestParam String name) {
        // Necesitas añadir este método a tu ProjectService.java y ProjectServiceImplement.java
        // Por ejemplo: List<ProjectDTO> findByNameContainingIgnoreCase(String name);
        // List<ProjectDTO> projects = projectService.findProjectsByNameContainingIgnoreCase(name);
        // return ResponseEntity.ok(projects);
        // Retorno de ejemplo si el método no está implementado aún:
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build(); // O implementa la lógica real
    }

    /**
     * Busca proyectos que inician en o después de una fecha dada.
     * GET /api/projects/search/start-date-after?date=YYYY-MM-DD
     * @param date La fecha mínima de inicio.
     * @return Lista de ProjectDTOs que cumplen la condición.
     */
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/search/start-date-after")
    public ResponseEntity<List<ProjectDTO>> searchProjectsByStartDateAfter(
            @RequestParam @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE) LocalDate date) {
        // Necesitas añadir este método a tu ProjectService.java y ProjectServiceImplement.java
        // Por ejemplo: List<ProjectDTO> findByStartDateGreaterThanEqual(LocalDate date);
        // List<ProjectDTO> projects = projectService.findProjectsByStartDateGreaterThanEqual(date);
        // return ResponseEntity.ok(projects);
        // Retorno de ejemplo si el método no está implementado aún:
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build(); // O implementa la lógica real
    }
}