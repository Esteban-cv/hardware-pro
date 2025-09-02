package com.mine.hardware_pro.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.mine.hardware_pro.model.Client;

public interface ClientRepository extends JpaRepository<Client, Long> {
}
