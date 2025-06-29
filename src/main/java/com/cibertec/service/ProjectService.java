package com.cibertec.service;

import java.time.LocalDate;
import java.util.List;

import com.cibertec.dto.ProjectDTO;
import com.cibertec.dto.TaskDTO;

public interface ProjectService {

	ProjectDTO createProject(ProjectDTO projectDTO);
    ProjectDTO getProjectById(Long id);
    List<ProjectDTO> getAllProjects();
    ProjectDTO updateProject(Long id, ProjectDTO projectDTO);
    void deleteProject(Long id);
    
 // Nuevo método para verificar si un usuario es el gerente de un proyecto específico
    boolean isProjectManager(Long projectId, String username);
    boolean isProjectManagerOfTask(Long taskId, String username);
    boolean isProjectManagerForTaskCreation(TaskDTO taskDTO, String username);
    
    List<ProjectDTO> findProjectsByNameContainingIgnoreCase(String name);
    List<ProjectDTO> findProjectsByStartDateGreaterThanEqual(LocalDate date);
}
