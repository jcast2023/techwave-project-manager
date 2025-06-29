package com.cibertec.service.serviceImpl;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cibertec.dto.ProjectDTO;
import com.cibertec.dto.TaskDTO;
import com.cibertec.dto.UserDTO; // <-- Asegúrate de importar UserDTO
import com.cibertec.entity.Project;
import com.cibertec.entity.Task;
import com.cibertec.entity.User;
import com.cibertec.exception.ResourceNotFoundException;
import com.cibertec.repository.ProjectRepository;
import com.cibertec.repository.TaskRepository;
import com.cibertec.repository.UserRepository;
import com.cibertec.service.ProjectService;

@Service
public class ProjectServiceImplement implements ProjectService {

    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final TaskRepository taskRepository;

    @Autowired
    public ProjectServiceImplement(ProjectRepository projectRepository, UserRepository userRepository, TaskRepository taskRepository) {
        this.projectRepository = projectRepository;
        this.userRepository = userRepository;
        this.taskRepository = taskRepository;
    }

    @Override
    @Transactional
    public ProjectDTO createProject(ProjectDTO projectDTO) {
        Project project = convertToEntity(projectDTO);

        // --- MODIFICACIÓN CLAVE AQUÍ ---
        if (projectDTO.getProjectManager() == null || projectDTO.getProjectManager().getId() == null) {
            throw new IllegalArgumentException("Project Manager ID cannot be null when creating a project.");
        }
        User projectManager = userRepository.findById(projectDTO.getProjectManager().getId()) // <-- ACCESO AL ID DEL DTO ANIDADO
                .orElseThrow(() -> new ResourceNotFoundException("User (Project Manager) not found with ID: " + projectDTO.getProjectManager().getId()));
        project.setProjectManager(projectManager);

        Project savedProject = projectRepository.save(project);
        return convertToDto(savedProject);
    }

    @Override
    @Transactional(readOnly = true)
    public ProjectDTO getProjectById(Long id) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with ID: " + id));
        return convertToDto(project);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProjectDTO> getAllProjects() {
        return projectRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ProjectDTO updateProject(Long id, ProjectDTO projectDTO) {
        return projectRepository.findById(id).map(existingProject -> {
            existingProject.setName(projectDTO.getName());
            existingProject.setDescription(projectDTO.getDescription());
            existingProject.setStartDate(projectDTO.getStartDate());
            existingProject.setExpectedEndDate(projectDTO.getExpectedEndDate());
            existingProject.setStatus(projectDTO.getStatus());
            existingProject.setBudget(projectDTO.getBudget());

            // --- MODIFICACIÓN CLAVE AQUÍ ---
            if (projectDTO.getProjectManager() != null && projectDTO.getProjectManager().getId() != null) {
                User projectManager = userRepository.findById(projectDTO.getProjectManager().getId()) // <-- ACCESO AL ID DEL DTO ANIDADO
                        .orElseThrow(() -> new ResourceNotFoundException("User (Project Manager) not found with ID: " + projectDTO.getProjectManager().getId()));
                existingProject.setProjectManager(projectManager);
            } else {
                throw new IllegalArgumentException("Project Manager ID cannot be null when updating a project with non-nullable manager.");
            }

            Project updatedProject = projectRepository.save(existingProject);
            return convertToDto(updatedProject);
        }).orElseThrow(() -> new ResourceNotFoundException("Project not found with ID: " + id));
    }

    @Override
    @Transactional
    public void deleteProject(Long id) {
        if (!projectRepository.existsById(id)) {
            throw new ResourceNotFoundException("Project not found with ID: " + id);
        }
        projectRepository.deleteById(id);
    }

    // --- Métodos de Verificación de Project Manager ---

    @Override
    @Transactional(readOnly = true)
    public boolean isProjectManager(Long projectId, String username) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with ID: " + projectId));

        return project.getProjectManager() != null && project.getProjectManager().getEmail().equals(username);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isProjectManagerOfTask(Long taskId, String username) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with ID: " + taskId));

        Project project = task.getProject();
        if (project == null) {
            return false;
        }

        return isProjectManager(project.getId(), username);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isProjectManagerForTaskCreation(TaskDTO taskDTO, String username) {
        if (taskDTO.getProjectId() == null) {
            throw new IllegalArgumentException("TaskDTO must have a projectId for manager verification.");
        }
        return isProjectManager(taskDTO.getProjectId(), username);
    }

    // --- Métodos de Búsqueda Implementados ---

    @Override
    @Transactional(readOnly = true)
    public List<ProjectDTO> findProjectsByNameContainingIgnoreCase(String name) {
        return projectRepository.findByNameContainingIgnoreCase(name).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProjectDTO> findProjectsByStartDateGreaterThanEqual(LocalDate date) {
        return projectRepository.findByStartDateGreaterThanEqual(date).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    // --- Métodos de Conversión (auxiliares) ---

    private Project convertToEntity(ProjectDTO projectDTO) {
        Project project = new Project();
        if (projectDTO.getId() != null) {
            project.setId(projectDTO.getId());
        }
        project.setName(projectDTO.getName());
        project.setDescription(projectDTO.getDescription());
        project.setStartDate(projectDTO.getStartDate());
        project.setExpectedEndDate(projectDTO.getExpectedEndDate());
        project.setStatus(projectDTO.getStatus());
        project.setBudget(projectDTO.getBudget());

        // El projectManager se setea en createProject o updateProject después de buscarlo por ID
        return project;
    }

    private ProjectDTO convertToDto(Project project) {
        ProjectDTO dto = new ProjectDTO();
        dto.setId(project.getId());
        dto.setName(project.getName());
        dto.setDescription(project.getDescription());
        dto.setStartDate(project.getStartDate());
        dto.setExpectedEndDate(project.getExpectedEndDate());
        dto.setStatus(project.getStatus());
        dto.setBudget(project.getBudget());

        // --- MODIFICACIÓN CLAVE AQUÍ para convertir a UserDTO anidado en la salida ---
        if (project.getProjectManager() != null) {
            UserDTO managerDTO = new UserDTO();
            managerDTO.setId(project.getProjectManager().getId());
            managerDTO.setUsername(project.getProjectManager().getUsername()); // Asumiendo que User tiene getUsername()
            managerDTO.setEmail(project.getProjectManager().getEmail()); // Asumiendo que User tiene getEmail()
            // Si tu entidad User tiene firstName y lastName, también puedes agregarlos aquí
            // managerDTO.setFirstName(project.getProjectManager().getFirstName());
            // managerDTO.setLastName(project.getProjectManager().getLastName());
            dto.setProjectManager(managerDTO); // <-- Asigna el DTO anidado
        }
        
        dto.setCreatedAt(project.getCreatedAt());
        dto.setLastUpdated(project.getLastUpdated());

        return dto;
    }
}