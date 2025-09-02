package com.mine.hardware_pro.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mine.hardware_pro.model.Location;

public interface LocationRepository extends JpaRepository<Location,Integer> {
}
