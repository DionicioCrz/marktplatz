package com.dionicio.marktplatz.dto;

import java.io.Serializable;
import java.math.BigDecimal;

public record ProductResponse(Long id, String name, String description, BigDecimal price, Integer stockQty, String category, String imageUrl) implements Serializable {
}
