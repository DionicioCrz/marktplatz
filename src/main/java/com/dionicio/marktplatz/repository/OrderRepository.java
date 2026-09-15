package com.dionicio.marktplatz.repository;

import com.dionicio.marktplatz.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
}
