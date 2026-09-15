package com.dionicio.marktplatz.repository;

import com.dionicio.marktplatz.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}
