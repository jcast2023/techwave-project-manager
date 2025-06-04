package com.cibertec.service.serviceImpl;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.cibertec.dto.MilestoneDTO;
import com.cibertec.entity.Milestone;
import com.cibertec.entity.Project;
import com.cibertec.exception.ResourceNotFoundException;
import com.cibertec.repository.MilestoneRepository;
import com.cibertec.repository.ProjectRepository;
import com.cibertec.service.MilestoneService;

import jakarta.transaction.Transactional;

@Service
public class MilestoneServiceImpl implements MilestoneService {

    private final MilestoneRepository milestoneRepository;
    private final ProjectRepository projectRepository; // Para verificar la existencia del proyecto
    private final ModelMapper modelMapper;

    // Inyección de dependencias por constructor (preferida)
    public MilestoneServiceImpl(MilestoneRepository milestoneRepository, ProjectRepository projectRepository, ModelMapper modelMapper) {
        this.milestoneRepository = milestoneRepository;
        this.projectRepository = projectRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    @Transactional // Asegura que el método se ejecute dentro de una transacción de base de datos
    public MilestoneDTO createMilestone(MilestoneDTO milestoneDTO) {
        // Verificar que el proyecto exista
        Project project = projectRepository.findById(milestoneDTO.getProjectId())
                .orElseThrow(() -> new ResourceNotFoundException("Project", "id", milestoneDTO.getProjectId()));

        Milestone milestone = modelMapper.map(milestoneDTO, Milestone.class);
        milestone.setProject(project); // Asigna la entidad Project

        Milestone savedMilestone = milestoneRepository.save(milestone);
        return modelMapper.map(savedMilestone, MilestoneDTO.class);
    }

    @Override
    public MilestoneDTO getMilestoneById(Long id) {
        Milestone milestone = milestoneRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Milestone", "id", id));
        return modelMapper.map(milestone, MilestoneDTO.class);
    }

    @Override
    public List<MilestoneDTO> getAllMilestones() {
        List<Milestone> milestones = milestoneRepository.findAll();
        return milestones.stream()
                .map(milestone -> modelMapper.map(milestone, MilestoneDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public MilestoneDTO updateMilestone(Long id, MilestoneDTO milestoneDTO) {
        Milestone existingMilestone = milestoneRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Milestone", "id", id));

        // Actualizar campos básicos
        existingMilestone.setName(milestoneDTO.getName());
        existingMilestone.setDescription(milestoneDTO.getDescription());
        existingMilestone.setDueDate(milestoneDTO.getDueDate());
        existingMilestone.setCompleted(milestoneDTO.getCompleted());

        // Actualizar proyecto si se proporciona un nuevo ID (opcional)
        if (milestoneDTO.getProjectId() != null && !milestoneDTO.getProjectId().equals(existingMilestone.getProject().getId())) {
            Project newProject = projectRepository.findById(milestoneDTO.getProjectId())
                    .orElseThrow(() -> new ResourceNotFoundException("Project", "id", milestoneDTO.getProjectId()));
            existingMilestone.setProject(newProject);
        }

        Milestone updatedMilestone = milestoneRepository.save(existingMilestone);
        return modelMapper.map(updatedMilestone, MilestoneDTO.class);
    }

    @Override
    @Transactional
    public void deleteMilestone(Long id) {
        Milestone milestone = milestoneRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Milestone", "id", id));
        milestoneRepository.delete(milestone);
    }

    @Override
    public List<MilestoneDTO> getMilestonesByProjectId(Long projectId) {
        List<Milestone> milestones = milestoneRepository.findByProjectId(projectId);
        return milestones.stream()
                .map(milestone -> modelMapper.map(milestone, MilestoneDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<MilestoneDTO> getPendingMilestones() {
        List<Milestone> milestones = milestoneRepository.findByCompletedFalse();
        return milestones.stream()
                .map(milestone -> modelMapper.map(milestone, MilestoneDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<MilestoneDTO> getMilestonesByDueDateLessThanEqual(LocalDate dueDate) {
        List<Milestone> milestones = milestoneRepository.findByDueDateLessThanEqual(dueDate);
        return milestones.stream()
                .map(milestone -> modelMapper.map(milestone, MilestoneDTO.class))
                .collect(Collectors.toList());
    }
}