package com.cibertec.service;

import java.util.List;

import com.cibertec.dto.TaskDTO;

public interface TaskService {

	TaskDTO createTask(TaskDTO taskDTO);
	TaskDTO getTaskById(Long id);
	List<TaskDTO> getAllTasks();
	TaskDTO updateTask(Long id, TaskDTO taskDTO);
	void deleteTask(Long id);
	List<TaskDTO> getTasksByProjectId(Long projectId);
	List<TaskDTO> getTasksByAssignedToId(Long assignedToId);
	List<TaskDTO> getTasksByStatus(String status);
	List<TaskDTO> getTasksByPriority(String priority);
	// Nuevo método para verificar si una tarea está asignada a un usuario específico
    boolean isTaskAssignedToUser(Long taskId, String username);
}
