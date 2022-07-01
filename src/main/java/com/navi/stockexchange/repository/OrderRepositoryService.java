package com.navi.stockexchange.repository;

import java.util.List;

public interface OrderRepositoryService<T> {

    void insertOrderInQueue(T order);

    T getFirstOrderInQueue();

    T removeFirstOrderInQueue();

    List<T> getAllOrdersInQueue();
}
