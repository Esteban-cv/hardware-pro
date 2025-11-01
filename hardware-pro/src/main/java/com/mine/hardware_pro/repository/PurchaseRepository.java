package com.mine.hardware_pro.repository;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import com.mine.hardware_pro.model.Purchase;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;


public interface PurchaseRepository extends JpaRepository<Purchase,Long> {
    List<Purchase> findByDateBetween(LocalDate startDate, LocalDate endDate, Sort sort);

    // Total de compras (para calcular balance)
    @Query("SELECT COALESCE(SUM(p.total), 0) FROM Purchase p WHERE p.status = 'RECIBIDA'")
    BigDecimal sumAllPurchases();
}
