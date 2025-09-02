package com.mine.hardware_pro.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.mine.hardware_pro.model.Sale;

public interface SaleRepository extends JpaRepository<Sale, Long> {
}
