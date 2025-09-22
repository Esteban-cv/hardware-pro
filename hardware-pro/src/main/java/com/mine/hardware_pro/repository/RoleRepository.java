package com.mine.hardware_pro.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mine.hardware_pro.model.Role;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role,Long> {
    Optional<Role> findByName(String name);
}
