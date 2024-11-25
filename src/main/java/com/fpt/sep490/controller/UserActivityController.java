package com.fpt.sep490.controller;

import com.fpt.sep490.model.UserActivity;
import com.fpt.sep490.service.UserActivityService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.Date;

@RestController
@RequestMapping("/user-activities")
public class UserActivityController {
    private final UserActivityService userActivityService;

    public UserActivityController(UserActivityService userActivityService) {
        this.userActivityService = userActivityService;
    }

    @GetMapping
    public ResponseEntity<Page<UserActivity>> getUserActivities(
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String activity,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "timestamp,desc") String[] sort
    ) {
        Sort sortBy = Sort.by(
                Arrays.stream(sort)
                        .map(order -> {
                            String[] parts = order.split(",");
                            return new Sort.Order(
                                    Sort.Direction.fromString(parts[1]),
                                    parts[0]
                            );
                        })
                        .toList()
        );

        Pageable pageable = PageRequest.of(page, size, sortBy);

        Page<UserActivity> userActivities = userActivityService.getFilteredUserActivities(
                username, activity, startDate, endDate, pageable
        );

        return ResponseEntity.ok(userActivities);
    }
}
