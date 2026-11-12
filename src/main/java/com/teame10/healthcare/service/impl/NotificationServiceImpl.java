package com.teame10.healthcare.service.impl;

import com.teame10.healthcare.entity.Notification;
import com.teame10.healthcare.entity.NotificationStatus;
import com.teame10.healthcare.entity.NotificationType;
import com.teame10.healthcare.entity.User;
import com.teame10.healthcare.exception.ResourceNotFoundException;
import com.teame10.healthcare.repository.NotificationRepository;
import com.teame10.healthcare.service.NotificationService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;

    public NotificationServiceImpl(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    @Override
    @Transactional
    public Notification sendNotification(User recipient, String phone, String email, String title, String message, NotificationType type) {
        Notification notification = new Notification(recipient, phone, email, title, message, type);
        notification.setStatus(NotificationStatus.PENDING);
        return notificationRepository.save(notification);
    }

    @Override
    public Notification getNotificationById(Long id) {
        return notificationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found with id: " + id));
    }

    @Override
    public List<Notification> getNotificationsForUser(Long userId) {
        return notificationRepository.findByRecipientUserIdOrderByCreatedAtDesc(userId);
    }

    @Override
    public Page<Notification> getNotificationsForUserPaged(Long userId, Pageable pageable) {
        return notificationRepository.findByRecipientUserId(userId, pageable);
    }

    @Override
    @Transactional
    public void markAsSent(Long notificationId) {
        Notification notification = getNotificationById(notificationId);
        notification.setStatus(NotificationStatus.SENT);
        notification.setSentAt(LocalDateTime.now());
        notificationRepository.save(notification);
    }

    @Override
    @Transactional
    public void markAsFailed(Long notificationId) {
        Notification notification = getNotificationById(notificationId);
        notification.setStatus(NotificationStatus.FAILED);
        notificationRepository.save(notification);
    }
}
