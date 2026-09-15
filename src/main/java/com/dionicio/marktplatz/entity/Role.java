package com.dionicio.marktplatz.entity;

import lombok.Getter;

@Getter
public enum Role {
    USER("Can browse products and place orders"),
    ADMIN("Can manage products, categories, and all orders");

    private final String description;

    Role(String description) {
        this.description = description;
    }

}