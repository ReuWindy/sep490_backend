package com.fpt.sep490.repository;

import com.fpt.sep490.model.UserActivity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserActivityRepository extends JpaRepository<UserActivity, Long> {
    Page<UserActivity> findAll(Specification<UserActivity> spec, Pageable pageable);
}
