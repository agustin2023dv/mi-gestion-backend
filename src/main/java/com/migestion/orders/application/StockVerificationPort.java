package com.migestion.orders.application;

public interface StockVerificationPort {

    boolean hasSufficientStock(Long productId, Integer quantity);
}