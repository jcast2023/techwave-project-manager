package com.cibertec.service.serviceImpl;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.cibertec.dto.ProjectDTO;
import com.cibertec.entity.Project;
import com.cibertec.exception.ResourceNotFoundException;
import com.cibertec.repository.ProjectRepository;
import com.cibertec.repository.UserRepository;
import com.cibertec.service.ProjectService;

import jakarta.transaction.Transactional;
@Service
public class ProjectServiceImpl implements ProjectService {

    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    public ProjectServiceImpl(ProjectRepository projectRepository, UserRepository userRepository, ModelMapper modelMapper) {
        this.projectRepository = projectRepository;
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    @Transactional
    public ProjectDTO createProject(ProjectDTO projectDTO) {
        Project project = modelMapper.map(projectDTO, Project.class);

        // Asignar el ProjectManager (User) a la entidad Project
        // Asumiendo que projectDTO.getProjectManager() no es nulo y tiene un ID válido
        if (projectDTO.getProjectManager() != null && projectDTO.getProjectManager().getId() != null) {
            userRepository.findById(projectDTO.getProjectManager().getId())
                .ifPresentOrElse(
                    project::setProjectManager,
                    () -> { throw new ResourceNotFoundException("User", "id", projectDTO.getProjectManager().getId()); }
                );
        } else {
            // Manejar el caso donde el projectManager no se proporciona o es inválido
            // Podrías lanzar una excepción diferente o simplemente no asignar el manager
            // Por ahora, lanzaremos una excepción si es requerido
            throw new IllegalArgumentException("Project Manager ID is required for project creation.");
        }


        Project savedProject = projectRepository.save(project);
        return modelMapper.map(savedProject, ProjectDTO.class);
    }

    @Override
    public ProjectDTO getProjectById(Long id) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Project", "id", id));
        return modelMapper.map(project, ProjectDTO.class);
    }

    @Override
    public List<ProjectDTO> getAllProjects() {
        List<Project> projects = projectRepository.findAll();
        return projects.stream()
                .map(project -> modelMapper.map(project, ProjectDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ProjectDTO updateProject(Long id, ProjectDTO projectDTO) {
        Project existingProject = projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Project", "id", id));

        // Actualizar campos de la entidad existente con los del DTO
        existingProject.setName(projectDTO.getName());
        existingProject.setDescription(projectDTO.getDescription());
        existingProject.setStartDate(projectDTO.getStartDate());
        existingProject.setExpectedEndDate(projectDTO.getExpectedEndDate());
        existingProject.setStatus(projectDTO.getStatus());
        existingProject.setBudget(projectDTO.getBudget());

        // Si se actualiza el gerente de proyecto
        if (projectDTO.getProjectManager() != null && projectDTO.getProjectManager().getId() != null) {
            userRepository.findById(projectDTO.getProjectManager().getId())
                .ifPresentOrElse(
                    existingProject::setProjectManager,
                    () -> { throw new ResourceNotFoundException("User", "id", projectDTO.getProjectManager().getId()); }
                );
        } else {
            // Si el projectManager se envía como nulo o no se envía, y es un campo obligatorio,
            // podrías lanzar una excepción o establecerlo a nulo si la lógica lo permite.
            // Por ahora, si se envía nulo, no lo actualizamos. Si es obligatorio, el front-end debería manejarlo.
            // Si quieres que se lance un error si se intenta poner a nulo un campo obligatorio,
            // la validación debería estar en el DTO o en un nivel superior.
        }

        Project updatedProject = projectRepository.save(existingProject);
        return modelMapper.map(updatedProject, ProjectDTO.class);
    }

    @Override
    @Transactional
    public void deleteProject(Long id) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Project", "id", id));
        projectRepository.delete(project);
    }

    // --- Implementación de los métodos de búsqueda ---

    @Override
    public List<ProjectDTO> findByNameContainingIgnoreCase(String name) {
        List<Project> projects = projectRepository.findByNameContainingIgnoreCase(name);
        return projects.stream()
                .map(project -> modelMapper.map(project, ProjectDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<ProjectDTO> findByStatus(String status) {
        List<Project> projects = projectRepository.findByStatus(status);
        return projects.stream()
                .map(project -> modelMapper.map(project, ProjectDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<ProjectDTO> findByProjectManagerId(Long projectManagerId) {
        List<Project> projects = projectRepository.findByProjectManagerId(projectManagerId);
        return projects.stream()
                .map(project -> modelMapper.map(project, ProjectDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<ProjectDTO> findByStartDateGreaterThanEqual(LocalDate startDate) {
        List<Project> projects = projectRepository.findByStartDateGreaterThanEqual(startDate);
        return projects.stream()
                .map(project -> modelMapper.map(project, ProjectDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<ProjectDTO> findByExpectedEndDateLessThanEqual(LocalDate expectedEndDate) {
        List<Project> projects = projectRepository.findByExpectedEndDateLessThanEqual(expectedEndDate);
        return projects.stream()
                .map(project -> modelMapper.map(project, ProjectDTO.class))
                .collect(Collectors.toList());
    }
}