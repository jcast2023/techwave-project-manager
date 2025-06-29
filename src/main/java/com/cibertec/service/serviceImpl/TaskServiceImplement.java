package com.cibertec.service.serviceImpl;

import com.cibertec.dto.TaskDTO;
// import com.cibertec.dto.UserDTO; // UserDTO no es necesario aquí si no lo anidamos directamente en TaskDTO de salida
import com.cibertec.entity.Task;
import com.cibertec.entity.Project;
import com.cibertec.entity.User;
import com.cibertec.repository.TaskRepository;
import com.cibertec.repository.ProjectRepository;
import com.cibertec.repository.UserRepository;
import com.cibertec.service.TaskService;
import com.cibertec.exception.ResourceNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementación del servicio para la gestión de tareas.
 * Contiene la lógica de negocio y la interacción con los repositorios.
 */
@Service
public class TaskServiceImplement implements TaskService {
	
	private final TaskRepository taskRepository;
	private final ProjectRepository projectRepository;
	private final UserRepository userRepository;

	@Autowired
	public TaskServiceImplement(TaskRepository taskRepository, ProjectRepository projectRepository, UserRepository userRepository) {
		this.taskRepository = taskRepository;
		this.projectRepository = projectRepository;
		this.userRepository = userRepository;
	}

	@Override
	@Transactional
	public TaskDTO createTask(TaskDTO taskDTO) {
		Task task = convertToEntity(taskDTO);

		// Asociar proyecto
		if (taskDTO.getProjectId() != null) {
			Project project = projectRepository.findById(taskDTO.getProjectId())
					.orElseThrow(() -> new ResourceNotFoundException("Project not found with ID: " + taskDTO.getProjectId()));
			task.setProject(project);
		} else {
            // Asumiendo que project_id en la tabla tareas es NOT NULL, es obligatorio.
            throw new IllegalArgumentException("Project ID cannot be null when creating a task.");
        }

		// Asociar usuario asignado
		if (taskDTO.getAssignedToId() != null) {
			User assignedUser = userRepository.findById(taskDTO.getAssignedToId())
					.orElseThrow(() -> new ResourceNotFoundException("Assigned user not found with ID: " + taskDTO.getAssignedToId()));
			task.setAssignedTo(assignedUser);
		}
        // Si assigned_to_usuario_id es nullable en la BD, no se necesita un 'else' aquí.
        // Si es NOT NULL y no se proporciona ID, la BD lanzará un error.

		Task savedTask = taskRepository.save(task);
		return convertToDto(savedTask);
	}

	@Override
	@Transactional(readOnly = true)
	public TaskDTO getTaskById(Long id) {
		Task task = taskRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Task not found with ID: " + id));
		return convertToDto(task);
	}

	@Override
	@Transactional(readOnly = true)
	public List<TaskDTO> getAllTasks() {
		return taskRepository.findAll().stream()
				.map(this::convertToDto)
				.collect(Collectors.toList());
	}

	@Override
	@Transactional
	public TaskDTO updateTask(Long id, TaskDTO taskDTO) {
		return taskRepository.findById(id).map(existingTask -> {
			existingTask.setName(taskDTO.getName());
			existingTask.setDescription(taskDTO.getDescription());
			existingTask.setDueDate(taskDTO.getDueDate()); // Usa getDueDate
            // *** NOTA IMPORTANTE: Tu TaskDTO no tiene el campo 'completed'.
            // Si la entidad Task tiene 'completed', este campo no se actualizará a través del DTO.
            // Si 'completed' se maneja de otra forma (por ejemplo, a través de un endpoint PATCH específico), ignora esto.
            // Si necesitas que se actualice aquí, agrega 'private boolean completed;' a tu TaskDTO.
            // existingTask.setCompleted(taskDTO.isCompleted()); // <-- ESTA LÍNEA SE ELIMINA O COMENTA
			existingTask.setStatus(taskDTO.getStatus());
			existingTask.setPriority(taskDTO.getPriority());

			// Actualizar proyecto
			if (taskDTO.getProjectId() != null) {
				Project project = projectRepository.findById(taskDTO.getProjectId())
						.orElseThrow(() -> new ResourceNotFoundException("Project not found with ID: " + taskDTO.getProjectId()));
				existingTask.setProject(project);
			} else {
                // Si el project_id de la tabla tareas es NOT NULL, establecer a null causará un error.
                // Si permites que una tarea se desasocie de un proyecto, entonces `existingTask.setProject(null);` es correcto.
                // Basado en tu tabla 'tareas', `proyecto_id` es NOT NULL. Por lo tanto, no debería ser null aquí.
                throw new IllegalArgumentException("Project ID cannot be null when updating a task.");
            }

			// Actualizar usuario asignado
			if (taskDTO.getAssignedToId() != null) {
				User assignedUser = userRepository.findById(taskDTO.getAssignedToId())
						.orElseThrow(() -> new ResourceNotFoundException("Assigned user not found with ID: " + taskDTO.getAssignedToId()));
				existingTask.setAssignedTo(assignedUser);
			} else {
                // Si assigned_to_usuario_id es nullable en la BD, puedes setearlo a null.
                // Si es NOT NULL y no se proporciona ID, la BD lanzará un error.
                existingTask.setAssignedTo(null); // Esto es válido si assigned_to_usuario_id es nullable en la BD.
            }

			Task updatedTask = taskRepository.save(existingTask);
			return convertToDto(updatedTask);
		}).orElseThrow(() -> new ResourceNotFoundException("Task not found with ID: " + id));
	}

	@Override
	@Transactional
	public void deleteTask(Long id) {
		if (!taskRepository.existsById(id)) {
			throw new ResourceNotFoundException("Task not found with ID: " + id);
		}
		taskRepository.deleteById(id);
	}

	@Override
	@Transactional(readOnly = true)
	public List<TaskDTO> getTasksByProjectId(Long projectId) {
		// Necesitarás añadir List<Task> findByProjectId(Long projectId); a TaskRepository
		return taskRepository.findByProjectId(projectId).stream()
				.map(this::convertToDto)
				.collect(Collectors.toList());
	}

	@Override
	@Transactional(readOnly = true)
	public List<TaskDTO> getTasksByAssignedToId(Long assignedToId) {
		// Necesitarás añadir List<Task> findByAssignedToId(Long assignedToId); a TaskRepository
		return taskRepository.findByAssignedToId(assignedToId).stream()
				.map(this::convertToDto)
				.collect(Collectors.toList());
	}

	@Override
	@Transactional(readOnly = true)
	public List<TaskDTO> getTasksByStatus(String status) {
		// Necesitarás añadir List<Task> findByStatus(String status); a TaskRepository
		return taskRepository.findByStatus(status).stream()
				.map(this::convertToDto)
				.collect(Collectors.toList());
	}

	@Override
	@Transactional(readOnly = true)
	public List<TaskDTO> getTasksByPriority(String priority) {
		// Necesitarás añadir List<Task> findByPriority(String priority); a TaskRepository
		return taskRepository.findByPriority(priority).stream()
				.map(this::convertToDto)
				.collect(Collectors.toList());
	}

	@Override
	@Transactional(readOnly = true)
	public boolean isTaskAssignedToUser(Long taskId, String username) {
		Task task = taskRepository.findById(taskId)
				.orElseThrow(() -> new ResourceNotFoundException("Task not found with ID: " + taskId));
		
		// Verifica si la tarea está asignada a un usuario y si el email de ese usuario coincide
		return task.getAssignedTo() != null && task.getAssignedTo().getEmail().equals(username);
	}

    // --- Métodos de Conversión Auxiliares ---

    // Convierte un TaskDTO a una entidad Task
    private Task convertToEntity(TaskDTO taskDTO) {
        Task task = new Task();
        if (taskDTO.getId() != null) {
            task.setId(taskDTO.getId());
        }
        task.setName(taskDTO.getName());
        task.setDescription(taskDTO.getDescription());
        task.setDueDate(taskDTO.getDueDate());
        // *** NOTA IMPORTANTE: Tu TaskDTO no tiene el campo 'completed'.
        // Si tu entidad Task sí tiene 'completed', este campo no se mapeará desde el DTO.
        // Si necesitas mapearlo, agrega 'private boolean completed;' a TaskDTO y 'task.setCompleted(taskDTO.isCompleted());' aquí.
        // task.setCompleted(taskDTO.isCompleted()); // <--- SE ELIMINA O COMENTA SI NO ESTÁ EN TaskDTO
        task.setStatus(taskDTO.getStatus());
        task.setPriority(taskDTO.getPriority());
        // createdAt y lastUpdated se manejan generalmente a nivel de entidad/JPA con @CreationTimestamp/@UpdateTimestamp

        // Project y AssignedTo se setean por separado en createTask y updateTask después de buscar las entidades completas.
        return task;
    }

    // Convierte una entidad Task a un TaskDTO
    private TaskDTO convertToDto(Task task) {
        TaskDTO dto = new TaskDTO();
        dto.setId(task.getId());
        dto.setName(task.getName());
        dto.setDescription(task.getDescription());
        dto.setDueDate(task.getDueDate());
        // *** NOTA IMPORTANTE: Tu TaskDTO no tiene el campo 'completed'.
        // Si tu entidad Task sí tiene 'completed', este campo no se mapeará al DTO.
        // Si necesitas mapearlo, agrega 'private boolean completed;' a TaskDTO y 'dto.setCompleted(task.isCompleted());' aquí.
        // dto.setCompleted(task.isCompleted()); // <--- SE ELIMINA O COMENTA SI NO ESTÁ EN TaskDTO
        dto.setStatus(task.getStatus());
        dto.setPriority(task.getPriority());
        dto.setCreatedAt(task.getCreatedAt());
        dto.setLastUpdated(task.getLastUpdated());

        // Si la tarea tiene un proyecto asociado, setea su ID en el DTO
        if (task.getProject() != null) {
            dto.setProjectId(task.getProject().getId());
        }

        // Si la tarea está asignada a un usuario, setea su ID en el DTO
        if (task.getAssignedTo() != null) {
            dto.setAssignedToId(task.getAssignedTo().getId());
            // *** NOTA IMPORTANTE: Tu TaskDTO no tiene los campos assignedToName ni assignedToEmail.
            // Estas líneas se comentan o eliminan si no existen en TaskDTO.
            // dto.setAssignedToName(task.getAssignedTo().getFirstName() + " " + task.getAssignedTo().getLastName());
            // dto.setAssignedToEmail(task.getAssignedTo().getEmail());
        }

        return dto;
    }
}