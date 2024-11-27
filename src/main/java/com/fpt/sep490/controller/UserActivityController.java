package com.fpt.sep490.controller;

import com.fpt.sep490.model.UserActivity;
import com.fpt.sep490.service.UserActivityService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

@RestController
@RequestMapping("/user-activities")
public class UserActivityController {
    private final UserActivityService userActivityService;

    public UserActivityController(UserActivityService userActivityService) {
        this.userActivityService = userActivityService;
    }

    @GetMapping("/getAll")
    public ResponseEntity<PagedModel<EntityModel<UserActivity>>> getUserActivities(
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String activity,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate,
            @RequestParam(defaultValue = "asc") String sortDirection,
            @RequestParam(defaultValue = "timestamp") String sortBy,
            @RequestParam(defaultValue = "1") int pageNumber,
            @RequestParam(defaultValue = "10") int pageSize,
            PagedResourcesAssembler<UserActivity> pagedResourcesAssembler) {
        Sort.Direction direction = Sort.Direction.fromString(sortDirection.toUpperCase());
        Sort sort = Sort.by(direction, sortBy);
        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize, sort);
        Page<UserActivity> userActivityPage = userActivityService.getFilteredUserActivities(
                username, activity, startDate, endDate, pageable);
        PagedModel<EntityModel<UserActivity>> pagedModel = pagedResourcesAssembler.toModel(userActivityPage);
        return ResponseEntity.ok(pagedModel);
    }
}