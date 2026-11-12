package com.teame10.healthcare.service;

import com.teame10.healthcare.entity.Notification;
import com.teame10.healthcare.entity.NotificationType;
import com.teame10.healthcare.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface NotificationService {

    Notification sendNotification(User recipient, String phone, String email, String title, String message, NotificationType type);

    Notification getNotificationById(Long id);

    List<Notification> getNotificationsForUser(Long userId);

    Page<Notification> getNotificationsForUserPaged(Long userId, Pageable pageable);

    void markAsSent(Long notificationId);

    void markAsFailed(Long notificationId);
}
