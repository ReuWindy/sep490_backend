package com.fpt.sep490.service;

import com.fpt.sep490.model.UserActivity;
import org.springframework.data.jpa.domain.Specification;

import java.util.Date;

public class UserActivitySpecification {
    public static Specification<UserActivity> hasUsername(String username) {
        return (root, query, criteriaBuilder) -> {
            if (username == null || username.isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("username"), username);
        };
    }

    public static Specification<UserActivity> hasActivity(String activity) {
        return (root, query, criteriaBuilder) -> {
            if (activity == null || activity.isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("activity"), activity);
        };
    }

    public static Specification<UserActivity> isBetweenDates(Date startDate, Date endDate) {
        return (root, query, criteriaBuilder) -> {
            if (startDate == null && endDate == null) {
                return criteriaBuilder.conjunction();
            }
            if (startDate != null && endDate != null) {
                return criteriaBuilder.between(root.get("timestamp"), startDate, endDate);
            } else if (startDate != null) {
                return criteriaBuilder.greaterThanOrEqualTo(root.get("timestamp"), startDate);
            } else {
                return criteriaBuilder.lessThanOrEqualTo(root.get("timestamp"), endDate);
            }
        };
    }
}
