package com.cibertec.service.serviceImpl;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

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

import jakarta.transaction.Transactional;

@Service // Marca esta clase como un componente de servicio de Spring
public class AttachmentServiceImpl implements AttachmentService {

    private final AttachmentRepository attachmentRepository;
    private final ProjectRepository projectRepository; // Para verificar la existencia del proyecto
    private final TaskRepository taskRepository;      // Para verificar la existencia de la tarea
    private final UserRepository userRepository;      // Para verificar el usuario que subió el archivo
    private final ModelMapper modelMapper;

    // Inyección de dependencias por constructor (preferida)
    public AttachmentServiceImpl(AttachmentRepository attachmentRepository,
                                 ProjectRepository projectRepository,
                                 TaskRepository taskRepository,
                                 UserRepository userRepository,
                                 ModelMapper modelMapper) {
        this.attachmentRepository = attachmentRepository;
        this.projectRepository = projectRepository;
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    @Transactional // Asegura que el método se ejecute dentro de una transacción de base de datos
    public AttachmentDTO createAttachment(AttachmentDTO attachmentDTO) {
        // Verificar que el usuario que subió el archivo exista
        User uploadedBy = userRepository.findById(attachmentDTO.getUploadedById())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", attachmentDTO.getUploadedById()));

        Project project = null;
        if (attachmentDTO.getProjectId() != null) {
            project = projectRepository.findById(attachmentDTO.getProjectId())
                    .orElseThrow(() -> new ResourceNotFoundException("Project", "id", attachmentDTO.getProjectId()));
        }

        Task task = null;
        if (attachmentDTO.getTaskId() != null) {
            task = taskRepository.findById(attachmentDTO.getTaskId())
                    .orElseThrow(() -> new ResourceNotFoundException("Task", "id", attachmentDTO.getTaskId()));
        }

        // Validar que el adjunto esté asociado a un proyecto O a una tarea
        if (project == null && task == null) {
            throw new IllegalArgumentException("El archivo adjunto debe estar asociado a un proyecto o a una tarea.");
        }
        // Validar que no esté asociado a AMBOS (si esa es la regla de negocio)
        if (project != null && task != null) {
            // Podrías lanzar una excepción o decidir cuál tiene prioridad
            // Por ahora, asumimos que solo uno puede ser no nulo
            throw new IllegalArgumentException("El archivo adjunto no puede estar asociado a un proyecto Y a una tarea simultáneamente.");
        }


        Attachment attachment = modelMapper.map(attachmentDTO, Attachment.class);
        attachment.setUploadedBy(uploadedBy);
        attachment.setProject(project);
        attachment.setTask(task);

        Attachment savedAttachment = attachmentRepository.save(attachment);
        return modelMapper.map(savedAttachment, AttachmentDTO.class);
    }

    @Override
    public AttachmentDTO getAttachmentById(Long id) {
        Attachment attachment = attachmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Attachment", "id", id));
        return modelMapper.map(attachment, AttachmentDTO.class);
    }

    @Override
    public List<AttachmentDTO> getAllAttachments() {
        List<Attachment> attachments = attachmentRepository.findAll();
        return attachments.stream()
                .map(attachment -> modelMapper.map(attachment, AttachmentDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public AttachmentDTO updateAttachment(Long id, AttachmentDTO attachmentDTO) {
        Attachment existingAttachment = attachmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Attachment", "id", id));

        // Actualizar campos básicos
        existingAttachment.setFileName(attachmentDTO.getFileName());
        existingAttachment.setContentType(attachmentDTO.getContentType());
        existingAttachment.setStoragePath(attachmentDTO.getStoragePath());
        existingAttachment.setSizeBytes(attachmentDTO.getSizeBytes());

        // Actualizar usuario que subió el archivo (si se proporciona un nuevo ID)
        if (attachmentDTO.getUploadedById() != null && !attachmentDTO.getUploadedById().equals(existingAttachment.getUploadedBy().getId())) {
            User newUploadedBy = userRepository.findById(attachmentDTO.getUploadedById())
                    .orElseThrow(() -> new ResourceNotFoundException("User", "id", attachmentDTO.getUploadedById()));
            existingAttachment.setUploadedBy(newUploadedBy);
        }

        // Actualizar proyecto (si se proporciona un nuevo ID o se desasocia)
        if (attachmentDTO.getProjectId() != null) {
            Project newProject = projectRepository.findById(attachmentDTO.getProjectId())
                    .orElseThrow(() -> new ResourceNotFoundException("Project", "id", attachmentDTO.getProjectId()));
            existingAttachment.setProject(newProject);
            existingAttachment.setTask(null); // Si se asocia a un proyecto, desasociar de tarea
        } else {
            existingAttachment.setProject(null); // Desasociar de proyecto
        }

        // Actualizar tarea (si se proporciona un nuevo ID o se desasocia)
        if (attachmentDTO.getTaskId() != null) {
            Task newTask = taskRepository.findById(attachmentDTO.getTaskId())
                    .orElseThrow(() -> new ResourceNotFoundException("Task", "id", attachmentDTO.getTaskId()));
            existingAttachment.setTask(newTask);
            existingAttachment.setProject(null); // Si se asocia a una tarea, desasociar de proyecto
        } else {
            existingAttachment.setTask(null); // Desasociar de tarea
        }

        // Validar que el adjunto esté asociado a un proyecto O a una tarea después de la actualización
        if (existingAttachment.getProject() == null && existingAttachment.getTask() == null) {
            throw new IllegalArgumentException("El archivo adjunto debe estar asociado a un proyecto o a una tarea después de la actualización.");
        }


        Attachment updatedAttachment = attachmentRepository.save(existingAttachment);
        return modelMapper.map(updatedAttachment, AttachmentDTO.class);
    }

    @Override
    @Transactional
    public void deleteAttachment(Long id) {
        Attachment attachment = attachmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Attachment", "id", id));
        attachmentRepository.delete(attachment);
    }

    @Override
    public List<AttachmentDTO> getAttachmentsByProjectId(Long projectId) {
        List<Attachment> attachments = attachmentRepository.findByProjectId(projectId);
        return attachments.stream()
                .map(attachment -> modelMapper.map(attachment, AttachmentDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<AttachmentDTO> getAttachmentsByTaskId(Long taskId) {
        List<Attachment> attachments = attachmentRepository.findByTaskId(taskId);
        return attachments.stream()
                .map(attachment -> modelMapper.map(attachment, AttachmentDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<AttachmentDTO> getAttachmentsByUploadedById(Long uploadedById) {
        List<Attachment> attachments = attachmentRepository.findByUploadedById(uploadedById);
        return attachments.stream()
                .map(attachment -> modelMapper.map(attachment, AttachmentDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<AttachmentDTO> getAttachmentsByContentType(String contentType) {
        List<Attachment> attachments = attachmentRepository.findByContentType(contentType);
        return attachments.stream()
                .map(attachment -> modelMapper.map(attachment, AttachmentDTO.class))
                .collect(Collectors.toList());
    }
}
