package com.fpt.sep490.service;

import com.fpt.sep490.model.Notification;
import com.fpt.sep490.repository.NotificationRepository;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class NotificationService {
    private final SimpMessagingTemplate messagingTemplate;
    private final NotificationRepository notificationRepository;

    public NotificationService(SimpMessagingTemplate messagingTemplate, NotificationRepository notificationRepository) {
        this.messagingTemplate = messagingTemplate;
        this.notificationRepository = notificationRepository;
    }

    public void notifyAdmin(String message) {
        messagingTemplate.convertAndSend("/topic/notification", message);
    }

    public void saveNotification(String message) {
        Notification notification = new Notification();
        notification.setMessage(message);
        notification.setDate(new Date());
        notification.setRead(false);
        notificationRepository.save(notification);
    }
}
