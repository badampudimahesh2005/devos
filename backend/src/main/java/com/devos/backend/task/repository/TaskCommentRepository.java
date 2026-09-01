package com.devos.backend.task.repository;

import com.devos.backend.task.entity.TaskComment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TaskCommentRepository
        extends JpaRepository<TaskComment, Long> {

    List<TaskComment> findByTaskIdOrderByCreatedAtAsc(
            Long taskId
    );

    Optional<TaskComment> findByIdAndTaskId(
            Long commentId,
            Long taskId
    );
}