package com.fpt.sep490.repository;

import com.fpt.sep490.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String username);
    User findUserByEmail(String email);
    boolean existsByEmail(String email);
    boolean existsByUsername(String username);
    User findUserByPhone(String phone);
}
