package com.devos.backend.task.repository;

import com.devos.backend.task.entity.Task;
import com.devos.backend.task.enums.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TaskRepository
        extends JpaRepository<Task, Long> {

    List<Task> findByProjectId(
            Long projectId
    );

    Optional<Task> findByProjectIdAndId(
            Long projectId,
            Long taskId
    );

    List<Task> findByProjectIdAndStatus(
            Long projectId,
            TaskStatus status
    );

    List<Task> findByAssigneeId(
            Long userId
    );
}