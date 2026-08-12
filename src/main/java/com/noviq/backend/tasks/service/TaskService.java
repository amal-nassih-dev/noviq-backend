package com.noviq.backend.tasks.service;

import java.util.List;

import com.noviq.backend.tasks.dto.TaskRequest;
import com.noviq.backend.tasks.dto.TaskResponse;
import com.noviq.backend.users.User;

public interface TaskService {
   
    public List<TaskResponse> fetchAll(Long orgId, Long projectId, User currentUser);
    public TaskResponse fetchById(Long orgId, Long projectId, Long taskId, User currentUser);
    public TaskResponse create(TaskRequest req, Long orgId, Long projectId, User currentUser);
    public TaskResponse update(TaskRequest req, Long orgId, Long projectId, Long taskId, User currentUser);
    public void delete(Long orgId, Long projectId, Long taskId, User currentUser);

}