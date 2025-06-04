package com.cibertec.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.cibertec.entity.Attachment;

@Repository
public interface AttachmentRepository extends JpaRepository<Attachment, Long>{
	
	List<Attachment> findByProjectId(Long projectId);
	List<Attachment> findByTaskId(Long taskId);
	List<Attachment> findByUploadedById(Long uploadedById);
	List<Attachment> findByContentType(String contentType);

}
