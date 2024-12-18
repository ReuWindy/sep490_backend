package com.fpt.sep490.repository;

import com.fpt.sep490.model.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    Optional<Category> findByName(String name);

    Page<Category> findAll(Specification<Category> specification, Pageable pageable);

    @Query("SELECT c.name, c.id FROM Category c")
    List<Object[]> getCategoryNameAndId();

    @Query("SELECT c.name FROM Category c")
    List<String> findAllCategoryNames();
}