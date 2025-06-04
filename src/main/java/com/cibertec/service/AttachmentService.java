package com.cibertec.service;

import java.util.List;

import com.cibertec.dto.AttachmentDTO;

public interface AttachmentService {
	
	AttachmentDTO createAttachment(AttachmentDTO attachmentDTO);
	AttachmentDTO getAttachmentById(Long id);
	List<AttachmentDTO> getAllAttachments();
	AttachmentDTO updateAttachment(Long id, AttachmentDTO attachmentDTO);
	void deleteAttachment(Long id);
	List<AttachmentDTO> getAttachmentsByProjectId(Long projectId);
	List<AttachmentDTO> getAttachmentsByTaskId(Long taskId);
	List<AttachmentDTO> getAttachmentsByUploadedById(Long uploadedById);
	List<AttachmentDTO> getAttachmentsByContentType(String contentType);
	
}
