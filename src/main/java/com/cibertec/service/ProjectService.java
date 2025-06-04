package com.cibertec.service;

import java.time.LocalDate;
import java.util.List;

import com.cibertec.dto.ProjectDTO;

public interface ProjectService {

	ProjectDTO createProject(ProjectDTO projectDTO);
    ProjectDTO getProjectById(Long id);
    List<ProjectDTO> getAllProjects();
    ProjectDTO updateProject(Long id, ProjectDTO projectDTO);
    void deleteProject(Long id);
    List<ProjectDTO> findByNameContainingIgnoreCase(String name);
    List<ProjectDTO> findByStatus(String status);
    List<ProjectDTO> findByProjectManagerId(Long projectManagerId);
    List<ProjectDTO> findByStartDateGreaterThanEqual(LocalDate startDate);
    List<ProjectDTO> findByExpectedEndDateLessThanEqual(LocalDate expectedEndDate);
}
