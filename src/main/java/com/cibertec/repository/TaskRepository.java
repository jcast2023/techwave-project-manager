package com.cibertec.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.cibertec.entity.Task;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long>{
	
	List<Task> findByProjectId(Long projectId);
	List<Task> findByAssignedToId(Long assignedToId);
	List<Task> findByStatus(String status);
	List<Task> findByPriority(String priority);

}
