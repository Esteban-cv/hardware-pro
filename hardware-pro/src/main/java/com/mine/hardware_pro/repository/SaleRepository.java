package com.mine.hardware_pro.repository;

import com.mine.hardware_pro.model.Purchase;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import com.mine.hardware_pro.model.Sale;

import java.time.LocalDate;
import java.util.List;

public interface SaleRepository extends JpaRepository<Sale, Long> {
    List<Sale> findByDateBetween(LocalDate startDate, LocalDate endDate, Sort sort);
}
