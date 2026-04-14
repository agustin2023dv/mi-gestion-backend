package com.migestion.orders.application;

public interface StockReservationPort {

    void reserveStock(Long productId, Integer quantity);
}