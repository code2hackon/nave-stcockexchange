package com.navi.stockexchange.repository.impl;

import com.navi.stockexchange.model.Order;
import com.navi.stockexchange.repository.OrderRepositoryService;

import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

public class BuyOrderRepositoryServiceImpl implements OrderRepositoryService<Order> {

    private PriorityQueue<Order> buyOrderQueue;

    public BuyOrderRepositoryServiceImpl(Comparator<Order> orderComparator) {
        buyOrderQueue = new PriorityQueue<>(orderComparator);
    }

    @Override
    public void insertOrderInQueue(Order order) {
        buyOrderQueue.add(order);
    }

    @Override
    public Order getFirstOrderInQueue() {
        return buyOrderQueue.peek();
    }

    @Override
    public Order removeFirstOrderInQueue() {
        return buyOrderQueue.poll();
    }

    @Override
    public List<Order> getAllOrdersInQueue() {
        throw new UnsupportedOperationException("Method unsupported for SellOrderRepositoryServiceImpl.");
    }
}
