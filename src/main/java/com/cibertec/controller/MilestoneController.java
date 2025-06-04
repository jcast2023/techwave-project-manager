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

import com.cibertec.dto.MilestoneDTO;
import com.cibertec.service.MilestoneService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/milestones")
public class MilestoneController {

    private final MilestoneService milestoneService;

    public MilestoneController(MilestoneService milestoneService) {
        this.milestoneService = milestoneService;
    }

    /**
     * Crea un nuevo hito.
     * POST /api/milestones
     * @param milestoneDTO El DTO del hito a crear.
     * @return ResponseEntity con el MilestoneDTO creado y estado HTTP 201 (Created).
     */
    @PostMapping
    public ResponseEntity<MilestoneDTO> createMilestone(@Valid @RequestBody MilestoneDTO milestoneDTO) {
        MilestoneDTO createdMilestone = milestoneService.createMilestone(milestoneDTO);
        return new ResponseEntity<>(createdMilestone, HttpStatus.CREATED);
    }

    /**
     * Obtiene un hito por su ID.
     * GET /api/milestones/{id}
     * @param id El ID del hito.
     * @return ResponseEntity con el MilestoneDTO y estado HTTP 200 (OK).
     */
    @GetMapping("/{id}")
    public ResponseEntity<MilestoneDTO> getMilestoneById(@PathVariable Long id) {
        MilestoneDTO milestone = milestoneService.getMilestoneById(id);
        return ResponseEntity.ok(milestone);
    }

    /**
     * Obtiene todos los hitos.
     * GET /api/milestones
     * @return ResponseEntity con una lista de MilestoneDTOs y estado HTTP 200 (OK).
     */
    @GetMapping
    public ResponseEntity<List<MilestoneDTO>> getAllMilestones() {
        List<MilestoneDTO> milestones = milestoneService.getAllMilestones();
        return ResponseEntity.ok(milestones);
    }

    /**
     * Actualiza un hito existente.
     * PUT /api/milestones/{id}
     * @param id El ID del hito a actualizar.
     * @param milestoneDTO El DTO del hito con la información actualizada.
     * @return ResponseEntity con el MilestoneDTO actualizado y estado HTTP 200 (OK).
     */
    @PutMapping("/{id}")
    public ResponseEntity<MilestoneDTO> updateMilestone(@PathVariable Long id, @Valid @RequestBody MilestoneDTO milestoneDTO) {
        MilestoneDTO updatedMilestone = milestoneService.updateMilestone(id, milestoneDTO);
        return ResponseEntity.ok(updatedMilestone);
    }

    /**
     * Elimina un hito por su ID.
     * DELETE /api/milestones/{id}
     * @param id El ID del hito a eliminar.
     * @return ResponseEntity con estado HTTP 204 (No Content).
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMilestone(@PathVariable Long id) {
        milestoneService.deleteMilestone(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    /**
     * Busca hitos por el ID del proyecto.
     * GET /api/milestones/search/by-project/{projectId}
     * @param projectId El ID del proyecto.
     * @return Lista de MilestoneDTOs que coinciden.
     */
    @GetMapping("/search/by-project/{projectId}")
    public ResponseEntity<List<MilestoneDTO>> getMilestonesByProjectId(@PathVariable Long projectId) {
        List<MilestoneDTO> milestones = milestoneService.getMilestonesByProjectId(projectId);
        return ResponseEntity.ok(milestones);
    }

    /**
     * Busca hitos pendientes (no completados).
     * GET /api/milestones/search/pending
     * @return Lista de MilestoneDTOs pendientes.
     */
    @GetMapping("/search/pending")
    public ResponseEntity<List<MilestoneDTO>> getPendingMilestones() {
        List<MilestoneDTO> milestones = milestoneService.getPendingMilestones();
        return ResponseEntity.ok(milestones);
    }

    /**
     * Busca hitos con una fecha límite anterior o igual a la fecha dada.
     * GET /api/milestones/search/due-date-before-or-equal?date=YYYY-MM-DD
     * @param date La fecha límite máxima.
     * @return Lista de MilestoneDTOs que cumplen la condición.
     */
    @GetMapping("/search/due-date-before-or-equal")
    public ResponseEntity<List<MilestoneDTO>> getMilestonesByDueDateLessThanEqual(@RequestParam @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE) LocalDate date) {
        List<MilestoneDTO> milestones = milestoneService.getMilestonesByDueDateLessThanEqual(date);
        return ResponseEntity.ok(milestones);
    }
}
