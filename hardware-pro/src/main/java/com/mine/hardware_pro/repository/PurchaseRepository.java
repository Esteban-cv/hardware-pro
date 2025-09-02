package com.mine.hardware_pro.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mine.hardware_pro.model.Purchase;


public interface PurchaseRepository extends JpaRepository<Purchase,Long> {

}
