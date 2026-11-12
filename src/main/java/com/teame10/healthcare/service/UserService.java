package com.teame10.healthcare.service;

import com.teame10.healthcare.entity.Role;
import com.teame10.healthcare.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface UserService {

    User createUser(User user);

    User getUserById(Long id);

    User getUserByUsername(String username);

    User getUserByEmail(String email);

    List<User> getUsersByRole(Role role);

    Page<User> getUsersByRole(Role role, Pageable pageable);

    User updateUser(Long id, User userDetails);

    void deactivateUser(Long id);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);
}
