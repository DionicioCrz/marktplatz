package com.dionicio.marktplatz.entity;

import lombok.Getter;

@Getter
public enum OrderStatus {
    PENDING("Order received, awaiting payment"),
    PAID("Payment confirmed"),
    SHIPPED("Package is on its way"),
    CANCELLED("Order was cancelled");

    private final String description;

    OrderStatus(String description) {
        this.description = description;
    }

}
