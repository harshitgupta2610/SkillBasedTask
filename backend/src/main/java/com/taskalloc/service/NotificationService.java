package com.taskalloc.service;

import com.taskalloc.model.*;
import com.taskalloc.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;

    public void notify(User user, String message) {
        notificationRepository.save(
            Notification.builder().user(user).message(message).build()
        );
    }

    public List<Notification> getForUser(Long userId) {
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    public long countUnread(Long userId) {
        return notificationRepository.countByUserIdAndRead(userId, false);
    }

    public void markAllRead(Long userId) {
        List<Notification> unread = notificationRepository.findByUserIdAndRead(userId, false);
        unread.forEach(n -> n.setRead(true));
        notificationRepository.saveAll(unread);
    }
}
