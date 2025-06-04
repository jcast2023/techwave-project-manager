package com.cibertec.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.cibertec.entity.Milestone;

@Repository
public interface MilestoneRepository extends JpaRepository<Milestone, Long>{
	
	List<Milestone> findByProjectId(Long projectId);
	List<Milestone> findByCompletedFalse();
	List<Milestone> findByDueDateLessThanEqual(LocalDate dueDate);
	
	

}
