package com.cibertec.service.serviceImpl;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cibertec.dto.AttachmentDTO;
import com.cibertec.entity.Attachment;
import com.cibertec.entity.Project;
import com.cibertec.entity.Task;
import com.cibertec.entity.User;

import com.cibertec.exception.ResourceNotFoundException;
import com.cibertec.repository.AttachmentRepository;
import com.cibertec.repository.ProjectRepository;
import com.cibertec.repository.TaskRepository;
import com.cibertec.repository.UserRepository;

import com.cibertec.service.AttachmentService;

@Service
public class AttachmentServiceImplement implements AttachmentService {

    private final AttachmentRepository attachmentRepository;
    private final UserRepository userRepository;
    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;

    @Autowired
    public AttachmentServiceImplement(AttachmentRepository attachmentRepository,
                                    UserRepository userRepository,
                                    TaskRepository taskRepository,
                                    ProjectRepository projectRepository) {
        this.attachmentRepository = attachmentRepository;
        this.userRepository = userRepository;
        this.taskRepository = taskRepository;
        this.projectRepository = projectRepository;
    }

    @Override
    @Transactional
    public AttachmentDTO createAttachment(AttachmentDTO attachmentDTO) {
        Attachment attachment = convertToEntity(attachmentDTO);
        Attachment savedAttachment = attachmentRepository.save(attachment);
        return convertToDto(savedAttachment);
    }

    @Override
    @Transactional(readOnly = true)
    public AttachmentDTO getAttachmentById(Long id) {
        Optional<Attachment> attachmentOptional = attachmentRepository.findById(id);
        if (attachmentOptional.isEmpty()) {
            throw new ResourceNotFoundException("Attachment not found with ID: " + id);
        }
        return convertToDto(attachmentOptional.get());
    }

    @Override
    @Transactional(readOnly = true)
    public List<AttachmentDTO> getAllAttachments() {
        List<Attachment> attachments = attachmentRepository.findAll();
        return attachments.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public AttachmentDTO updateAttachment(Long id, AttachmentDTO attachmentDTO) {
        return attachmentRepository.findById(id).map(existingAttachment -> {
            existingAttachment.setFileName(attachmentDTO.getFileName());
            existingAttachment.setContentType(attachmentDTO.getContentType());
            existingAttachment.setStoragePath(attachmentDTO.getStoragePath());
            existingAttachment.setSizeBytes(attachmentDTO.getSizeBytes());
            // Si uploadedAt es @CreationTimestamp, no lo actualices a menos que sea un requisito específico.
            // existingAttachment.setUploadedAt(attachmentDTO.getUploadedAt());

            // Actualizar relaciones (debes buscar y asignar las entidades completas si el DTO tiene los IDs)
            if (attachmentDTO.getUploadedById() != null) {
                User user = userRepository.findById(attachmentDTO.getUploadedById())
                                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + attachmentDTO.getUploadedById()));
                existingAttachment.setUploadedBy(user);
            } else {
                 // Dependiendo de tu lógica, podrías querer nullificar la relación si se envía null en la actualización
                 // o mantenerla si no se envía un nuevo ID.
                 existingAttachment.setUploadedBy(null); // Esto anularía la relación si se envía null.
            }
            if (attachmentDTO.getTaskId() != null) {
                Task task = taskRepository.findById(attachmentDTO.getTaskId())
                                .orElseThrow(() -> new ResourceNotFoundException("Task not found with ID: " + attachmentDTO.getTaskId()));
                existingAttachment.setTask(task);
            } else {
                existingAttachment.setTask(null);
            }
            if (attachmentDTO.getProjectId() != null) {
                Project project = projectRepository.findById(attachmentDTO.getProjectId())
                                    .orElseThrow(() -> new ResourceNotFoundException("Project not found with ID: " + attachmentDTO.getProjectId()));
                existingAttachment.setProject(project);
            } else {
                existingAttachment.setProject(null);
            }


            Attachment updatedAttachment = attachmentRepository.save(existingAttachment);
            return convertToDto(updatedAttachment);
        }).orElseThrow(() -> new ResourceNotFoundException("Attachment not found with ID: " + id));
    }

    @Override
    @Transactional
    public void deleteAttachment(Long id) {
        if (!attachmentRepository.existsById(id)) {
            throw new ResourceNotFoundException("Attachment not found with ID: " + id);
        }
        attachmentRepository.deleteById(id);
    }

    // --- Métodos de Conversión (auxiliares) ---
    private AttachmentDTO convertToDto(Attachment attachment) {
        AttachmentDTO dto = new AttachmentDTO();
        dto.setId(attachment.getId());
        dto.setFileName(attachment.getFileName());
        dto.setContentType(attachment.getContentType());
        dto.setStoragePath(attachment.getStoragePath());
        dto.setSizeBytes(attachment.getSizeBytes());
        dto.setUploadedAt(attachment.getUploadedAt());

        if (attachment.getProject() != null) {
            dto.setProjectId(attachment.getProject().getId());
        }
        if (attachment.getTask() != null) {
            dto.setTaskId(attachment.getTask().getId());
        }
        if (attachment.getUploadedBy() != null) {
            dto.setUploadedById(attachment.getUploadedBy().getId());
        }

        return dto;
    }

    private Attachment convertToEntity(AttachmentDTO attachmentDTO) {
        Attachment attachment = new Attachment();
        if (attachmentDTO.getId() != null) {
            attachment.setId(attachmentDTO.getId());
        }
        attachment.setFileName(attachmentDTO.getFileName());
        attachment.setContentType(attachmentDTO.getContentType());
        attachment.setStoragePath(attachmentDTO.getStoragePath());
        attachment.setSizeBytes(attachmentDTO.getSizeBytes());
        // No asignes uploadedAt aquí si usas @CreationTimestamp en la entidad
        // attachment.setUploadedAt(attachmentDTO.getUploadedAt());

        // Manejo de relaciones
        if (attachmentDTO.getUploadedById() != null) {
            User user = userRepository.findById(attachmentDTO.getUploadedById())
                            .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + attachmentDTO.getUploadedById()));
            attachment.setUploadedBy(user);
        } else {
            // uploadedById es mandatory por nullable=false en la entidad.
            // Si el DTO puede enviar null, la entidad Attachment.java debe tener nullable=true para usuario_subida_id
            throw new IllegalArgumentException("uploadedById is mandatory for creating an Attachment.");
        }

        if (attachmentDTO.getTaskId() != null) {
            Task task = taskRepository.findById(attachmentDTO.getTaskId())
                            .orElseThrow(() -> new ResourceNotFoundException("Task not found with ID: " + attachmentDTO.getTaskId()));
            attachment.setTask(task);
        }
        // Para taskId y projectId, ya que @JoinColumn no especifica nullable=false, son nullable por defecto.
        // Si el DTO envía null, no se asigna la relación, y la columna en DB se guardará como NULL.

        if (attachmentDTO.getProjectId() != null) {
            Project project = projectRepository.findById(attachmentDTO.getProjectId())
                                .orElseThrow(() -> new ResourceNotFoundException("Project not found with ID: " + attachmentDTO.getProjectId()));
            attachment.setProject(project);
        }

        return attachment;
    }
}