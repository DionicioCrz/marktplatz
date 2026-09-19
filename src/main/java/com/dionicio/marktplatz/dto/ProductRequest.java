package com.dionicio.marktplatz.dto;

import java.math.BigDecimal;

public record ProductRequest(String name, String description, BigDecimal price, Integer stockQuantity, Long categoryId, String imageUrl) { }
