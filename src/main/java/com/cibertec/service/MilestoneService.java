package com.cibertec.service;

import java.time.LocalDate;
import java.util.List;

import com.cibertec.dto.MilestoneDTO;

public interface MilestoneService {
	
	MilestoneDTO createMilestone(MilestoneDTO milestoneDTO);
	MilestoneDTO getMilestoneById(Long id);
	List<MilestoneDTO> getAllMilestones();
	MilestoneDTO updateMilestone(Long id, MilestoneDTO milestoneDTO);
	void deleteMilestone(Long id);
	List<MilestoneDTO> getMilestonesByProjectId(Long projectId);
	List<MilestoneDTO> getPendingMilestones();
	List<MilestoneDTO> getMilestonesByDueDateLessThanEqual(LocalDate dueDate);

}
