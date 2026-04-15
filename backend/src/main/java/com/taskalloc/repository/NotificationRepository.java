package com.taskalloc.repository;

import com.taskalloc.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByUserIdOrderByCreatedAtDesc(Long userId);
    List<Notification> findByUserIdAndRead(Long userId, boolean read);
    long countByUserIdAndRead(Long userId, boolean read);
}
