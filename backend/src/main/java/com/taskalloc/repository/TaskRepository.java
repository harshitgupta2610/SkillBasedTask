package com.taskalloc.repository;

import com.taskalloc.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByAssignedToId(Long userId);
    List<Task> findByCreatedById(Long userId);
    List<Task> findByStatus(Task.Status status);

    @Query("SELECT COUNT(t) FROM Task t WHERE t.assignedTo.id = :userId AND t.status IN ('ASSIGNED', 'IN_PROGRESS')")
    long countActiveTasksByUser(Long userId);
}
