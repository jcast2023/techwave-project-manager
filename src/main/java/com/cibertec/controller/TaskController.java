package com.cibertec.controller;

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

import com.cibertec.dto.TaskDTO;
import com.cibertec.service.TaskService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    /**
     * Crea una nueva tarea.
     * POST /api/tasks
     * @param taskDTO El DTO de la tarea a crear.
     * @return ResponseEntity con el TaskDTO creado y estado HTTP 201 (Created).
     */
    @PostMapping
    public ResponseEntity<TaskDTO> createTask(@Valid @RequestBody TaskDTO taskDTO) {
        TaskDTO createdTask = taskService.createTask(taskDTO);
        return new ResponseEntity<>(createdTask, HttpStatus.CREATED);
    }

    /**
     * Obtiene una tarea por su ID.
     * GET /api/tasks/{id}
     * @param id El ID de la tarea.
     * @return ResponseEntity con el TaskDTO y estado HTTP 200 (OK).
     */
    @GetMapping("/{id}")
    public ResponseEntity<TaskDTO> getTaskById(@PathVariable Long id) {
        TaskDTO task = taskService.getTaskById(id);
        return ResponseEntity.ok(task);
    }

    /**
     * Obtiene todas las tareas.
     * GET /api/tasks
     * @return ResponseEntity con una lista de TaskDTOs y estado HTTP 200 (OK).
     */
    @GetMapping
    public ResponseEntity<List<TaskDTO>> getAllTasks() {
        List<TaskDTO> tasks = taskService.getAllTasks();
        return ResponseEntity.ok(tasks);
    }

    /**
     * Actualiza una tarea existente.
     * PUT /api/tasks/{id}
     * @param id El ID de la tarea a actualizar.
     * @param taskDTO El DTO de la tarea con la información actualizada.
     * @return ResponseEntity con el TaskDTO actualizado y estado HTTP 200 (OK).
     */
    @PutMapping("/{id}")
    public ResponseEntity<TaskDTO> updateTask(@PathVariable Long id, @Valid @RequestBody TaskDTO taskDTO) {
        TaskDTO updatedTask = taskService.updateTask(id, taskDTO);
        return ResponseEntity.ok(updatedTask);
    }

    /**
     * Elimina una tarea por su ID.
     * DELETE /api/tasks/{id}
     * @param id El ID de la tarea a eliminar.
     * @return ResponseEntity con estado HTTP 204 (No Content).
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
        taskService.deleteTask(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    /**
     * Busca tareas por el ID del proyecto.
     * GET /api/tasks/search/by-project/{projectId}
     * @param projectId El ID del proyecto.
     * @return Lista de TaskDTOs que coinciden.
     */
    @GetMapping("/search/by-project/{projectId}")
    public ResponseEntity<List<TaskDTO>> getTasksByProjectId(@PathVariable Long projectId) {
        List<TaskDTO> tasks = taskService.getTasksByProjectId(projectId);
        return ResponseEntity.ok(tasks);
    }

    /**
     * Busca tareas asignadas a un usuario.
     * GET /api/tasks/search/by-assigned-to/{assignedToId}
     * @param assignedToId El ID del usuario asignado.
     * @return Lista de TaskDTOs que coinciden.
     */
    @GetMapping("/search/by-assigned-to/{assignedToId}")
    public ResponseEntity<List<TaskDTO>> getTasksByAssignedToId(@PathVariable Long assignedToId) {
        List<TaskDTO> tasks = taskService.getTasksByAssignedToId(assignedToId);
        return ResponseEntity.ok(tasks);
    }

    /**
     * Busca tareas por estado.
     * GET /api/tasks/search/by-status?status=valor
     * @param status El estado de la tarea.
     * @return Lista de TaskDTOs que coinciden.
     */
    @GetMapping("/search/by-status")
    public ResponseEntity<List<TaskDTO>> getTasksByStatus(@RequestParam String status) {
        List<TaskDTO> tasks = taskService.getTasksByStatus(status);
        return ResponseEntity.ok(tasks);
    }

    /**
     * Busca tareas por prioridad.
     * GET /api/tasks/search/by-priority?priority=valor
     * @param priority La prioridad de la tarea.
     * @return Lista de TaskDTOs que coinciden.
     */
    @GetMapping("/search/by-priority")
    public ResponseEntity<List<TaskDTO>> getTasksByPriority(@RequestParam String priority) {
        List<TaskDTO> tasks = taskService.getTasksByPriority(priority);
        return ResponseEntity.ok(tasks);
    }
}

