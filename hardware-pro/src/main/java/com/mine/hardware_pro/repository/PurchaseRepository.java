package com.mine.hardware_pro.repository;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import com.mine.hardware_pro.model.Purchase;

import java.time.LocalDate;
import java.util.List;


public interface PurchaseRepository extends JpaRepository<Purchase,Long> {
    List<Purchase> findByDateBetween(LocalDate startDate, LocalDate endDate, Sort sort);

}
