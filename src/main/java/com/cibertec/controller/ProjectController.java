package com.cibertec.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.cibertec.dto.ProjectDTO;
import com.cibertec.service.ProjectService;

import jakarta.validation.Valid;

@RestController // Indica que esta clase es un controlador REST
@RequestMapping("/api/projects") // Define la ruta base para todos los endpoints de este controlador
public class ProjectController {

    private final ProjectService projectService;

    // Inyección de dependencias por constructor
    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    /**
     * Crea un nuevo proyecto.
     * POST /api/projects
     * @param projectDTO El DTO del proyecto a crear.
     * @return ResponseEntity con el ProjectDTO creado y estado HTTP 201 (Created).
     */
    @PostMapping
    public ResponseEntity<ProjectDTO> createProject(@Valid @RequestBody ProjectDTO projectDTO) {
        ProjectDTO createdProject = projectService.createProject(projectDTO);
        return new ResponseEntity<>(createdProject, HttpStatus.CREATED);
    }

    /**
     * Obtiene un proyecto por su ID.
     * GET /api/projects/{id}
     * @param id El ID del proyecto.
     * @return ResponseEntity con el ProjectDTO y estado HTTP 200 (OK).
     */
    @GetMapping("/{id}")
    public ResponseEntity<ProjectDTO> getProjectById(@PathVariable Long id) {
        ProjectDTO project = projectService.getProjectById(id);
        return ResponseEntity.ok(project); // Retorna 200 OK
    }

    /**
     * Obtiene todos los proyectos.
     * GET /api/projects
     * @return ResponseEntity con una lista de ProjectDTOs y estado HTTP 200 (OK).
     */
    @GetMapping
    public ResponseEntity<List<ProjectDTO>> getAllProjects() {
        List<ProjectDTO> projects = projectService.getAllProjects();
        return ResponseEntity.ok(projects); // Retorna 200 OK
    }

    /**
     * Actualiza un proyecto existente.
     * PUT /api/projects/{id}
     * @param id El ID del proyecto a actualizar.
     * @param projectDTO El DTO del proyecto con la información actualizada.
     * @return ResponseEntity con el ProjectDTO actualizado y estado HTTP 200 (OK).
     */
    @PutMapping("/{id}")
    public ResponseEntity<ProjectDTO> updateProject(@PathVariable Long id, @Valid @RequestBody ProjectDTO projectDTO) {
        ProjectDTO updatedProject = projectService.updateProject(id, projectDTO);
        return ResponseEntity.ok(updatedProject); // Retorna 200 OK
    }

    /**
     * Elimina un proyecto por su ID.
     * DELETE /api/projects/{id}
     * @param id El ID del proyecto a eliminar.
     * @return ResponseEntity con estado HTTP 204 (No Content).
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProject(@PathVariable Long id) {
        projectService.deleteProject(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT); // Retorna 204 No Content
    }

    /**
     * Busca proyectos por nombre (parcial, ignorando mayúsculas/minúsculas).
     * GET /api/projects/search/by-name?name=valor
     * @param name El nombre parcial del proyecto.
     * @return Lista de ProjectDTOs que coinciden.
     */
    @GetMapping("/search/by-name")
    public ResponseEntity<List<ProjectDTO>> getProjectsByName(@RequestParam String name) {
        List<ProjectDTO> projects = projectService.findByNameContainingIgnoreCase(name);
        return ResponseEntity.ok(projects);
    }

    /**
     * Busca proyectos por estado.
     * GET /api/projects/search/by-status?status=valor
     * @param status El estado del proyecto.
     * @return Lista de ProjectDTOs que coinciden.
     */
    @GetMapping("/search/by-status")
    public ResponseEntity<List<ProjectDTO>> getProjectsByStatus(@RequestParam String status) {
        List<ProjectDTO> projects = projectService.findByStatus(status);
        return ResponseEntity.ok(projects);
    }

    /**
     * Busca proyectos por el ID del gerente de proyecto.
     * GET /api/projects/search/by-manager/{managerId}
     * @param managerId El ID del gerente de proyecto.
     * @return Lista de ProjectDTOs que coinciden.
     */
    @GetMapping("/search/by-manager/{managerId}")
    public ResponseEntity<List<ProjectDTO>> getProjectsByManagerId(@PathVariable Long managerId) {
        List<ProjectDTO> projects = projectService.findByProjectManagerId(managerId);
        return ResponseEntity.ok(projects);
    }

    /**
     * Busca proyectos que inician en o después de una fecha dada.
     * GET /api/projects/search/start-date-after?date=YYYY-MM-DD
     * @param date La fecha mínima de inicio.
     * @return Lista de ProjectDTOs que cumplen la condición.
     */
    @GetMapping("/search/start-date-after")
    public ResponseEntity<List<ProjectDTO>> getProjectsByStartDateAfter(@RequestParam @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE) LocalDate date) {
        List<ProjectDTO> projects = projectService.findByStartDateGreaterThanEqual(date);
        return ResponseEntity.ok(projects);
    }

    /**
     * Busca proyectos que tienen una fecha de fin esperada en o antes de una fecha dada.
     * GET /api/projects/search/expected-end-date-before?date=YYYY-MM-DD
     * @param date La fecha máxima de fin esperada.
     * @return Lista de ProjectDTOs que cumplen la condición.
     */
    @GetMapping("/search/expected-end-date-before")
    public ResponseEntity<List<ProjectDTO>> getProjectsByExpectedEndDateBefore(@RequestParam @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE) LocalDate date) {
        List<ProjectDTO> projects = projectService.findByExpectedEndDateLessThanEqual(date);
        return ResponseEntity.ok(projects);
    }
}
