package com.cibertec.service.serviceImpl;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cibertec.dto.MilestoneDTO;
import com.cibertec.entity.Milestone;
import com.cibertec.entity.Project;
import com.cibertec.exception.ResourceNotFoundException;
import com.cibertec.repository.MilestoneRepository;
import com.cibertec.repository.ProjectRepository;
import com.cibertec.service.MilestoneService;

@Service
public class MilestoneServiceImplement implements MilestoneService {

    private final MilestoneRepository milestoneRepository;
    private final ProjectRepository projectRepository;

    @Autowired
    public MilestoneServiceImplement(MilestoneRepository milestoneRepository, ProjectRepository projectRepository) {
        this.milestoneRepository = milestoneRepository;
        this.projectRepository = projectRepository;
    }

    @Override
    @Transactional
    public MilestoneDTO createMilestone(MilestoneDTO milestoneDTO) {
        Milestone milestone = convertToEntity(milestoneDTO);
        // Aquí no necesitas setear completed si ya lo inicializas en el constructor de la entidad
        // o si confías en el valor que viene en el DTO (si es que lo envías).
        // Si no se envía en el DTO, y la DB es nullable, será null. Si es NOT NULL, debes darle un valor.
        // La entidad ya tiene 'completed = false' por defecto.
        Milestone savedMilestone = milestoneRepository.save(milestone);
        return convertToDto(savedMilestone);
    }

    @Override
    @Transactional(readOnly = true)
    public MilestoneDTO getMilestoneById(Long id) {
        Milestone milestone = milestoneRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Milestone not found with ID: " + id));
        return convertToDto(milestone);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MilestoneDTO> getAllMilestones() {
        return milestoneRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public MilestoneDTO updateMilestone(Long id, MilestoneDTO milestoneDTO) {
        return milestoneRepository.findById(id).map(existingMilestone -> {
            existingMilestone.setName(milestoneDTO.getName());
            existingMilestone.setDescription(milestoneDTO.getDescription());
            existingMilestone.setDueDate(milestoneDTO.getDueDate());
            existingMilestone.setCompleted(milestoneDTO.getCompleted()); // <-- ¡Cambiado! Usar getCompleted
            // No hay 'completedDate' en tu entidad Milestone, si lo necesitas añádelo
            // existingMilestone.setCompletedDate(milestoneDTO.getCompletedDate());

            if (milestoneDTO.getProjectId() != null) {
                Project project = projectRepository.findById(milestoneDTO.getProjectId())
                        .orElseThrow(() -> new ResourceNotFoundException("Project not found with ID: " + milestoneDTO.getProjectId()));
                existingMilestone.setProject(project);
            } else {
                // Si proyecto_id es nullable=false en DB, no puedes setearlo a null aquí
                // Si es nullable=false, el DTO de actualización SIEMPRE debe enviar un projectId
                throw new IllegalArgumentException("Milestone must be associated with a Project.");
            }

            Milestone updatedMilestone = milestoneRepository.save(existingMilestone);
            return convertToDto(updatedMilestone);
        }).orElseThrow(() -> new ResourceNotFoundException("Milestone not found with ID: " + id));
    }

    @Override
    @Transactional
    public void deleteMilestone(Long id) {
        if (!milestoneRepository.existsById(id)) {
            throw new ResourceNotFoundException("Milestone not found with ID: " + id);
        }
        milestoneRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MilestoneDTO> getMilestonesByProjectId(Long projectId) {
        return milestoneRepository.findByProjectId(projectId).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<MilestoneDTO> getPendingMilestones() {
        return milestoneRepository.findByCompleted(false).stream() // <-- ¡Cambiado! Buscar los NO completados
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<MilestoneDTO> getMilestonesByDueDateLessThanEqual(LocalDate dueDate) {
        return milestoneRepository.findByDueDateLessThanEqual(dueDate).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    // --- Métodos de Conversión (auxiliares) ---
    private Milestone convertToEntity(MilestoneDTO milestoneDTO) {
        Milestone milestone = new Milestone();
        if (milestoneDTO.getId() != null) {
            milestone.setId(milestoneDTO.getId());
        }
        milestone.setName(milestoneDTO.getName());
        milestone.setDescription(milestoneDTO.getDescription());
        milestone.setDueDate(milestoneDTO.getDueDate());
        milestone.setCompleted(milestoneDTO.getCompleted() != null ? milestoneDTO.getCompleted() : false); // <-- Usar getCompleted. Si es null en DTO, default a false
        // Si no hay 'completedDate' en tu entidad, esta línea no va
        // milestone.setCompletedDate(milestoneDTO.getCompletedDate());

        if (milestoneDTO.getProjectId() != null) {
            Project project = projectRepository.findById(milestoneDTO.getProjectId())
                    .orElseThrow(() -> new ResourceNotFoundException("Project not found with ID: " + milestoneDTO.getProjectId()));
            milestone.setProject(project);
        } else {
            // Ya que proyecto_id es nullable=false en DB, no puedes crear un hito sin proyecto.
            // Si el DTO no envía projectId, lanza una excepción.
            throw new IllegalArgumentException("Milestone must be associated with a Project.");
        }

        return milestone;
    }

    private MilestoneDTO convertToDto(Milestone milestone) {
        MilestoneDTO dto = new MilestoneDTO();
        dto.setId(milestone.getId());
        dto.setName(milestone.getName());
        dto.setDescription(milestone.getDescription());
        dto.setDueDate(milestone.getDueDate());
        dto.setCompleted(milestone.getCompleted()); // <-- Usar getCompleted
        // Si no hay 'completedDate' en tu entidad, esta línea no va
        // dto.setCompletedDate(milestone.getCompletedDate());

        if (milestone.getProject() != null) {
            dto.setProjectId(milestone.getProject().getId());
        }
        dto.setCreatedAt(milestone.getCreatedAt());
        dto.setLastUpdated(milestone.getLastUpdated()); // <-- Añadido
        return dto;
    }
}