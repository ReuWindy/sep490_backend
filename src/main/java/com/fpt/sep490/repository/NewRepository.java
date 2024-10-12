package com.fpt.sep490.repository;

import com.fpt.sep490.model.News;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NewRepository extends JpaRepository<News, Integer> {
}
