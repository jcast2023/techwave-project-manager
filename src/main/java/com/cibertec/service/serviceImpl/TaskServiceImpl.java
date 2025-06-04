package com.cibertec.service.serviceImpl;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.cibertec.dto.TaskDTO;
import com.cibertec.entity.Project;
import com.cibertec.entity.Task;
import com.cibertec.entity.User;
import com.cibertec.exception.ResourceNotFoundException;
import com.cibertec.repository.ProjectRepository;
import com.cibertec.repository.TaskRepository;
import com.cibertec.repository.UserRepository;
import com.cibertec.service.TaskService;

import jakarta.transaction.Transactional;

@Service
public class TaskServiceImpl implements TaskService{
	
	 private final TaskRepository taskRepository;
	    private final ProjectRepository projectRepository; // Para verificar la existencia del proyecto
	    private final UserRepository userRepository;      // Para verificar la existencia del usuario asignado
	    private final ModelMapper modelMapper;

	    // Inyección de dependencias por constructor (preferida)
	    public TaskServiceImpl(TaskRepository taskRepository, ProjectRepository projectRepository, UserRepository userRepository, ModelMapper modelMapper) {
	        this.taskRepository = taskRepository;
	        this.projectRepository = projectRepository;
	        this.userRepository = userRepository;
	        this.modelMapper = modelMapper;
	    }

	    @Override
	    @Transactional // Asegura que el método se ejecute dentro de una transacción de base de datos
	    public TaskDTO createTask(TaskDTO taskDTO) {
	        // Verificar que el proyecto exista
	        Project project = projectRepository.findById(taskDTO.getProjectId())
	                .orElseThrow(() -> new ResourceNotFoundException("Project", "id", taskDTO.getProjectId()));

	        User assignedTo = null;
	        // Verificar que el usuario asignado exista, si se proporciona
	        if (taskDTO.getAssignedToId() != null) {
	            assignedTo = userRepository.findById(taskDTO.getAssignedToId())
	                    .orElseThrow(() -> new ResourceNotFoundException("User", "id", taskDTO.getAssignedToId()));
	        }

	        Task task = modelMapper.map(taskDTO, Task.class);
	        task.setProject(project); // Asigna la entidad Project
	        task.setAssignedTo(assignedTo); // Asigna la entidad User (puede ser null)

	        Task savedTask = taskRepository.save(task);
	        return modelMapper.map(savedTask, TaskDTO.class);
	    }

	    @Override
	    public TaskDTO getTaskById(Long id) {
	        Task task = taskRepository.findById(id)
	                .orElseThrow(() -> new ResourceNotFoundException("Task", "id", id));
	        return modelMapper.map(task, TaskDTO.class);
	    }

	    @Override
	    public List<TaskDTO> getAllTasks() {
	        List<Task> tasks = taskRepository.findAll();
	        return tasks.stream()
	                .map(task -> modelMapper.map(task, TaskDTO.class))
	                .collect(Collectors.toList());
	    }

	    @Override
	    @Transactional
	    public TaskDTO updateTask(Long id, TaskDTO taskDTO) {
	        Task existingTask = taskRepository.findById(id)
	                .orElseThrow(() -> new ResourceNotFoundException("Task", "id", id));

	        // Actualizar campos básicos
	        existingTask.setName(taskDTO.getName());
	        existingTask.setDescription(taskDTO.getDescription());
	        existingTask.setDueDate(taskDTO.getDueDate());
	        existingTask.setStatus(taskDTO.getStatus());
	        existingTask.setPriority(taskDTO.getPriority());

	        // Actualizar proyecto si se proporciona un nuevo ID (opcional, pero buena práctica si se permite reasignar)
	        if (taskDTO.getProjectId() != null && !taskDTO.getProjectId().equals(existingTask.getProject().getId())) {
	            Project newProject = projectRepository.findById(taskDTO.getProjectId())
	                    .orElseThrow(() -> new ResourceNotFoundException("Project", "id", taskDTO.getProjectId()));
	            existingTask.setProject(newProject);
	        }

	        // Actualizar usuario asignado si se proporciona un nuevo ID
	        if (taskDTO.getAssignedToId() != null) {
	            User newAssignedTo = userRepository.findById(taskDTO.getAssignedToId())
	                    .orElseThrow(() -> new ResourceNotFoundException("User", "id", taskDTO.getAssignedToId()));
	            existingTask.setAssignedTo(newAssignedTo);
	        } else {
	            // Si assignedToId es nulo en el DTO, significa que se quiere desasignar la tarea
	            existingTask.setAssignedTo(null);
	        }

	        Task updatedTask = taskRepository.save(existingTask);
	        return modelMapper.map(updatedTask, TaskDTO.class);
	    }

	    @Override
	    @Transactional
	    public void deleteTask(Long id) {
	        Task task = taskRepository.findById(id)
	                .orElseThrow(() -> new ResourceNotFoundException("Task", "id", id));
	        taskRepository.delete(task);
	    }

	    @Override
	    public List<TaskDTO> getTasksByProjectId(Long projectId) {
	        List<Task> tasks = taskRepository.findByProjectId(projectId);
	        return tasks.stream()
	                .map(task -> modelMapper.map(task, TaskDTO.class))
	                .collect(Collectors.toList());
	    }

	    @Override
	    public List<TaskDTO> getTasksByAssignedToId(Long assignedToId) {
	        List<Task> tasks = taskRepository.findByAssignedToId(assignedToId);
	        return tasks.stream()
	                .map(task -> modelMapper.map(task, TaskDTO.class))
	                .collect(Collectors.toList());
	    }

	    @Override
	    public List<TaskDTO> getTasksByStatus(String status) {
	        List<Task> tasks = taskRepository.findByStatus(status);
	        return tasks.stream()
	                .map(task -> modelMapper.map(task, TaskDTO.class))
	                .collect(Collectors.toList());
	    }

	    @Override
	    public List<TaskDTO> getTasksByPriority(String priority) {
	        List<Task> tasks = taskRepository.findByPriority(priority);
	        return tasks.stream()
	                .map(task -> modelMapper.map(task, TaskDTO.class))
	                .collect(Collectors.toList());
	    }
	}
