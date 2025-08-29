package com.mine.hardware_pro.repository;


import org.springframework.data.jpa.repository.JpaRepository;

import com.mine.hardware_pro.model.Unit;

public interface UnitRepository extends JpaRepository<Unit,Integer> {
}
