package com.cibertec.repository;

import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.cibertec.entity.Milestone;

public interface MilestoneRepository extends JpaRepository<Milestone, Long> {
    List<Milestone> findByProjectId(Long projectId);
    List<Milestone> findByCompleted(Boolean completed); // <-- ¡Cambiado! Buscar por campo 'completed'
    List<Milestone> findByDueDateLessThanEqual(LocalDate dueDate);
}