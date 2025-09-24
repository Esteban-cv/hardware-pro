package com.mine.hardware_pro.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.mine.hardware_pro.model.SaleDetail;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SaleDetailRepository extends JpaRepository<SaleDetail,Integer> {
    @Query("SELECT sd FROM SaleDetail sd WHERE sd.sale.idSale = :saleId")
    List<SaleDetail> findBySaleId(@Param("saleId") Long saleId);
}
