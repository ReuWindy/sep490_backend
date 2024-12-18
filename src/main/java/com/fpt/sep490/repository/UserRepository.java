package com.fpt.sep490.repository;

import com.fpt.sep490.model.User;
import com.fpt.sep490.model.UserType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String username);

    User findUserByEmail(String email);

    User findUserByPhone(String phone);

    boolean existsByEmail(String email);

    boolean existsByUsername(String username);

    List<User> findAllByUserTypeAndActive(UserType userType, Boolean active);

    Page<User> findAll(Specification<User> specification, Pageable pageable);
}