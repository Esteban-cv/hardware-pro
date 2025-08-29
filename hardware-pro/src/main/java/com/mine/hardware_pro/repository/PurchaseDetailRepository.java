package com.mine.hardware_pro.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mine.hardware_pro.model.PurchaseDetail;

public interface PurchaseDetailRepository extends JpaRepository<PurchaseDetail,Integer> {
}
