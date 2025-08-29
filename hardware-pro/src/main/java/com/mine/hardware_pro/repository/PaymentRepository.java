package com.mine.hardware_pro.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mine.hardware_pro.model.Payment;

public interface PaymentRepository extends JpaRepository<Payment,Integer> {
}
