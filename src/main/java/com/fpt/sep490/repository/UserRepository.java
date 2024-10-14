package com.fpt.sep490.repository;

import com.fpt.sep490.model.User;
import com.fpt.sep490.model.UserType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String username);
    User findUserByEmail(String email);
    boolean existsByEmail(String email);
    boolean existsByUsername(String username);
    User findUserByPhone(String phone);
    List<User> findAllByUserType(UserType userType);
}
