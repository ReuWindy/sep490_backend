package com.fpt.sep490.service;

import com.fpt.sep490.model.UserActivity;
import com.fpt.sep490.repository.UserActivityRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.Date;

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
        userActivity.setTimestamp(new Date());
        userActivityRepository.save(userActivity);
        String message = String.format("Time: %s, User: %s, Action: %s, Object: %s",
                userActivity.getTimestamp(),
                userActivity.getUsername(),
                userActivity.getActivity(),
                userActivity.getObject());
        notificationService.notifyAdmin(message);
    }

    public Page<UserActivity> getFilteredUserActivities(
            String username,
            String activity,
            Date startDate,
            Date endDate,
            Pageable pageable) {

        Specification<UserActivity> spec = Specification.where(UserActivitySpecification.hasUsername(username))
                .and(UserActivitySpecification.hasActivity(activity))
                .and(UserActivitySpecification.isBetweenDates(startDate, endDate));

        return userActivityRepository.findAll(spec, pageable);
    }
}
