package com.fpt.sep490.repository;

import com.fpt.sep490.model.News;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NewRepository extends JpaRepository<News, Integer> {
    Page<News> findAll(Specification<News> specification, Pageable pageable);
}