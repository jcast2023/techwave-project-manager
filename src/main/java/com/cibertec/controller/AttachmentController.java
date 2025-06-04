package com.cibertec.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.cibertec.dto.AttachmentDTO;
import com.cibertec.service.AttachmentService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/attachments")
public class AttachmentController {

    private final AttachmentService attachmentService;

    public AttachmentController(AttachmentService attachmentService) {
        this.attachmentService = attachmentService;
    }

    /**
     * Crea un nuevo archivo adjunto.
     * POST /api/attachments
     * @param attachmentDTO El DTO del archivo adjunto a crear.
     * @return ResponseEntity con el AttachmentDTO creado y estado HTTP 201 (Created).
     */
    @PostMapping
    public ResponseEntity<AttachmentDTO> createAttachment(@Valid @RequestBody AttachmentDTO attachmentDTO) {
        AttachmentDTO createdAttachment = attachmentService.createAttachment(attachmentDTO);
        return new ResponseEntity<>(createdAttachment, HttpStatus.CREATED);
    }

    /**
     * Obtiene un archivo adjunto por su ID.
     * GET /api/attachments/{id}
     * @param id El ID del archivo adjunto.
     * @return ResponseEntity con el AttachmentDTO y estado HTTP 200 (OK).
     */
    @GetMapping("/{id}")
    public ResponseEntity<AttachmentDTO> getAttachmentById(@PathVariable Long id) {
        AttachmentDTO attachment = attachmentService.getAttachmentById(id);
        return ResponseEntity.ok(attachment);
    }

    /**
     * Obtiene todos los archivos adjuntos.
     * GET /api/attachments
     * @return ResponseEntity con una lista de AttachmentDTOs y estado HTTP 200 (OK).
     */
    @GetMapping
    public ResponseEntity<List<AttachmentDTO>> getAllAttachments() {
        List<AttachmentDTO> attachments = attachmentService.getAllAttachments();
        return ResponseEntity.ok(attachments);
    }

    /**
     * Actualiza un archivo adjunto existente.
     * PUT /api/attachments/{id}
     * @param id El ID del archivo adjunto a actualizar.
     * @param attachmentDTO El DTO del archivo adjunto con la información actualizada.
     * @return ResponseEntity con el AttachmentDTO actualizado y estado HTTP 200 (OK).
     */
    @PutMapping("/{id}")
    public ResponseEntity<AttachmentDTO> updateAttachment(@PathVariable Long id, @Valid @RequestBody AttachmentDTO attachmentDTO) {
        AttachmentDTO updatedAttachment = attachmentService.updateAttachment(id, attachmentDTO);
        return ResponseEntity.ok(updatedAttachment);
    }

    /**
     * Elimina un archivo adjunto por su ID.
     * DELETE /api/attachments/{id}
     * @param id El ID del archivo adjunto a eliminar.
     * @return ResponseEntity con estado HTTP 204 (No Content).
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAttachment(@PathVariable Long id) {
        attachmentService.deleteAttachment(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    /**
     * Busca archivos adjuntos por el ID del proyecto.
     * GET /api/attachments/search/by-project/{projectId}
     * @param projectId El ID del proyecto.
     * @return Lista de AttachmentDTOs que coinciden.
     */
    @GetMapping("/search/by-project/{projectId}")
    public ResponseEntity<List<AttachmentDTO>> getAttachmentsByProjectId(@PathVariable Long projectId) {
        List<AttachmentDTO> attachments = attachmentService.getAttachmentsByProjectId(projectId);
        return ResponseEntity.ok(attachments);
    }

    /**
     * Busca archivos adjuntos por el ID de la tarea.
     * GET /api/attachments/search/by-task/{taskId}
     * @param taskId El ID de la tarea.
     * @return Lista de AttachmentDTOs que coinciden.
     */
    @GetMapping("/search/by-task/{taskId}")
    public ResponseEntity<List<AttachmentDTO>> getAttachmentsByTaskId(@PathVariable Long taskId) {
        List<AttachmentDTO> attachments = attachmentService.getAttachmentsByTaskId(taskId);
        return ResponseEntity.ok(attachments);
    }

    /**
     * Busca archivos adjuntos subidos por un usuario.
     * GET /api/attachments/search/by-uploader/{uploadedById}
     * @param uploadedById El ID del usuario que subió el archivo.
     * @return Lista de AttachmentDTOs que coinciden.
     */
    @GetMapping("/search/by-uploader/{uploadedById}")
    public ResponseEntity<List<AttachmentDTO>> getAttachmentsByUploadedById(@PathVariable Long uploadedById) {
        List<AttachmentDTO> attachments = attachmentService.getAttachmentsByUploadedById(uploadedById);
        return ResponseEntity.ok(attachments);
    }

    /**
     * Busca archivos adjuntos por tipo de contenido.
     * GET /api/attachments/search/by-content-type?contentType=valor
     * @param contentType El tipo de contenido del archivo.
     * @return Lista de AttachmentDTOs que coinciden.
     */
    @GetMapping("/search/by-content-type")
    public ResponseEntity<List<AttachmentDTO>> getAttachmentsByContentType(@RequestParam String contentType) {
        List<AttachmentDTO> attachments = attachmentService.getAttachmentsByContentType(contentType);
        return ResponseEntity.ok(attachments);
    }
}
