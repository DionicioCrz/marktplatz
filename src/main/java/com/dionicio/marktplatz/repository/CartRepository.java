package com.dionicio.marktplatz.repository;

import com.dionicio.marktplatz.entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartRepository extends JpaRepository<Cart, Long> {
}
