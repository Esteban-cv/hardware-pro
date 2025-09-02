package com.mine.hardware_pro.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mine.hardware_pro.model.Supplier;

public interface SupplierRepository extends JpaRepository<Supplier,Long> {
}
