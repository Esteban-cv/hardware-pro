package com.mine.hardware_pro.repository;

import com.mine.hardware_pro.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository  extends JpaRepository<User,Long> {
    Optional<User> findByEmail(String email);
}
