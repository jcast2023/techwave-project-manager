package com.cibertec.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cibertec.entity.Project;

public interface ProjectRepository extends JpaRepository<Project, Long>{
	
	List<Project> findByNameContainingIgnoreCase(String name);
	List<Project> findByStatus(String status);
	List<Project> findByProjectManagerId(Long projectManagerId);
	List<Project> findByStartDateGreaterThanEqual(LocalDate startDate);
	List<Project> findByExpectedEndDateLessThanEqual(LocalDate expectedEndDate);

}
