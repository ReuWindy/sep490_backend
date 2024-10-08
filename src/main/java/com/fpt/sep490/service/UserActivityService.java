package com.fpt.sep490.service;

import com.fpt.sep490.model.UserActivity;
import com.fpt.sep490.repository.UserActivityRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class UserActivityService {
    private final NotificationService notificationService;
    private final UserActivityRepository userActivityRepository;

    public UserActivityService(NotificationService notificationService, UserActivityRepository userActivityRepository) {
        this.notificationService = notificationService;
        this.userActivityRepository = userActivityRepository;
    }

    public void logAndNotifyAdmin(String username, String activity, String object) {
        UserActivity userActivity = new UserActivity();
        userActivity.setUsername(username);
        userActivity.setActivity(activity);
        userActivity.setObject(object);
        userActivity.setTimestamp(LocalDateTime.now());
        userActivityRepository.save(userActivity);
        String message = String.format("Time: %s, User: %s, Action: %s, Object: %s",
                userActivity.getTimestamp(),
                userActivity.getUsername(),
                userActivity.getActivity(),
                userActivity.getObject());
        notificationService.notifyAdmin(message);
    }
}
