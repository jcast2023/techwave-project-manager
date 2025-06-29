package com.cibertec.controller;

import com.cibertec.dto.TaskDTO;
import com.cibertec.service.TaskService;
import com.cibertec.service.ProjectService; // Importar ProjectService
import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException; // Importar para excepciones de acceso denegado
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication; // Importar Authentication
import org.springframework.security.core.GrantedAuthority; // Importar GrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder; // Importar SecurityContextHolder
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors; // Importar Collectors para el stream

import org.slf4j.Logger; // Importar Logger
import org.slf4j.LoggerFactory; // Importar LoggerFactory

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private static final Logger logger = LoggerFactory.getLogger(TaskController.class); // Instancia del logger

    private final TaskService taskService;
    private final ProjectService projectService; // ¡Ahora inyectamos ProjectService!

    // Constructor actualizado para inyectar TaskService Y ProjectService
    public TaskController(TaskService taskService, ProjectService projectService) {
        this.taskService = taskService;
        this.projectService = projectService; // Inicializamos projectService
    }

    /**
     * Crea una nueva tarea.
     * Restricción inicial: Solo accesible por usuarios con los roles 'ADMIN' o 'PROJECT_MANAGER'.
     * Lógica adicional: Se verifica que el usuario Project Manager sea el gestor del proyecto de la tarea.
     * POST /api/tasks
     * @param taskDTO El DTO de la tarea a crear.
     * @return ResponseEntity con el TaskDTO creado y estado HTTP 201 (Created).
     */
    @PreAuthorize("hasAnyRole('ADMIN', 'PROJECT_MANAGER')") // Simplificamos la condición inicial aquí
    @PostMapping
    public ResponseEntity<TaskDTO> createTask(@Valid @RequestBody TaskDTO taskDTO) {
        logger.info("--- [TaskController] INICIO: Solicitud para crear tarea.");

        // Obtener la información de autenticación del contexto de seguridad
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName(); // Nombre de usuario del usuario autenticado
        
        // Obtener y loggear todas las autoridades del usuario
        String authorities = authentication.getAuthorities().stream()
                                .map(GrantedAuthority::getAuthority)
                                .collect(Collectors.joining(", "));

        logger.info("--- [TaskController] Usuario autenticado en el contexto de seguridad: {}", username);
        logger.info("--- [TaskController] Autoridades del usuario en el contexto de seguridad: [{}]", authorities);

        // Verificar si el usuario autenticado tiene el rol ADMIN
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        logger.info("--- [TaskController] ¿Tiene el rol 'ROLE_ADMIN' en el contexto? {}", isAdmin);

        // Si el usuario no es ADMIN, debemos verificar si es PROJECT_MANAGER del proyecto de la tarea
        if (!isAdmin) {
            logger.info("--- [TaskController] Usuario NO es ADMIN. Verificando rol PROJECT_MANAGER y asignación al proyecto.");
            // Verificar si el usuario tiene el rol PROJECT_MANAGER
            boolean isProjectManagerRole = authentication.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_PROJECT_MANAGER"));
            logger.info("--- [TaskController] ¿Tiene el rol 'ROLE_PROJECT_MANAGER' en el contexto? {}", isProjectManagerRole);

            // Si es PROJECT_MANAGER, verificar si es el Project Manager del proyecto específico de la tarea
            if (isProjectManagerRole) {
                logger.info("--- [TaskController] Usuario es PROJECT_MANAGER. Verificando si es PM del proyecto ID: {}", taskDTO.getProjectId());
                // Aquí usamos el método isProjectManagerForTaskCreation que creamos
                if (!projectService.isProjectManagerForTaskCreation(taskDTO, username)) {
                    logger.warn("--- [TaskController] Acceso denegado por lógica de Project Manager: No es PM del proyecto asignado a la tarea.");
                    throw new AccessDeniedException("Acceso denegado: No es administrador ni Project Manager del proyecto asignado a la tarea.");
                }
                logger.info("--- [TaskController] Verificación de Project Manager para el proyecto EXITOSA.");
            } else {
                // Si no es ADMIN ni PROJECT_MANAGER (o PROJECT_MANAGER sin rol), denegar acceso
                logger.warn("--- [TaskController] Acceso denegado por roles insuficientes: No es ADMIN ni PROJECT_MANAGER.");
                throw new AccessDeniedException("Acceso denegado: Roles insuficientes para crear una tarea.");
            }
        } else {
            logger.info("--- [TaskController] Usuario es ADMIN. Autorización interna concedida.");
        }

        // Si la autorización pasa, proceder a crear la tarea
        TaskDTO createdTask = taskService.createTask(taskDTO);
        logger.info("--- [TaskController] FIN: Tarea creada con éxito. ID: {}", createdTask.getId());
        return new ResponseEntity<>(createdTask, HttpStatus.CREATED);
    }

    /**
     * Obtiene una tarea por su ID.
     * Accesible por cualquier usuario autenticado.
     * GET /api/tasks/{id}
     * @param id El ID de la tarea.
     * @return ResponseEntity con el TaskDTO y estado HTTP 200 (OK).
     */
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{id}")
    public ResponseEntity<TaskDTO> getTaskById(@PathVariable Long id) {
        TaskDTO task = taskService.getTaskById(id);
        return ResponseEntity.ok(task);
    }

    /**
     * Obtiene todas las tareas.
     * Accesible por cualquier usuario autenticado.
     * GET /api/tasks
     * @return ResponseEntity con una lista de TaskDTOs y estado HTTP 200 (OK).
     */
    @PreAuthorize("isAuthenticated()")
    @GetMapping
    public ResponseEntity<List<TaskDTO>> getAllTasks() {
        List<TaskDTO> tasks = taskService.getAllTasks();
        return ResponseEntity.ok(tasks);
    }

    /**
     * Actualiza una tarea existente.
     * Solo accesible por usuarios con el rol 'ADMIN', el 'PROJECT_MANAGER' del proyecto asociado a la tarea,
     * o el 'DEVELOPER' asignado a esa tarea.
     * PUT /api/tasks/{id}
     * @param id El ID de la tarea a actualizar.
     * @param taskDTO El DTO de la tarea con la información actualizada.
     * @return ResponseEntity con el TaskDTO actualizado y estado HTTP 200 (OK).
     */
    @PreAuthorize("hasRole('ADMIN') or @projectService.isProjectManagerOfTask(#id, authentication.name) or @taskService.isTaskAssignedToUser(#id, authentication.name)")
    @PutMapping("/{id}")
    public ResponseEntity<TaskDTO> updateTask(@PathVariable Long id, @Valid @RequestBody TaskDTO taskDTO) {
        TaskDTO updatedTask = taskService.updateTask(id, taskDTO);
        return ResponseEntity.ok(updatedTask);
    }

    /**
     * Elimina una tarea por su ID.
     * Solo accesible por usuarios con el rol 'ADMIN' o el 'PROJECT_MANAGER' del proyecto asociado a la tarea.
     * DELETE /api/tasks/{id}
     * @param id El ID de la tarea a eliminar.
     * @return ResponseEntity con estado HTTP 204 (No Content).
     */
    @PreAuthorize("hasRole('ADMIN') or @projectService.isProjectManagerOfTask(#id, authentication.name)")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
        taskService.deleteTask(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    // Puedes añadir más endpoints de búsqueda aquí y aplicar PreAuthorize según sea necesario
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/by-project/{projectId}")
    public ResponseEntity<List<TaskDTO>> getTasksByProjectId(@PathVariable Long projectId) {
        List<TaskDTO> tasks = taskService.getTasksByProjectId(projectId);
        return ResponseEntity.ok(tasks);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/by-assigned-user/{assignedUserId}")
    public ResponseEntity<List<TaskDTO>> getTasksByAssignedUser(@PathVariable Long assignedUserId) {
        List<TaskDTO> tasks = taskService.getTasksByAssignedToId(assignedUserId);
        return ResponseEntity.ok(tasks);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/by-status")
    public ResponseEntity<List<TaskDTO>> getTasksByStatus(@RequestParam String status) {
        List<TaskDTO> tasks = taskService.getTasksByStatus(status);
        return ResponseEntity.ok(tasks);
    }
}