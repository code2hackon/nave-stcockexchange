package com.navi.stockexchange.repository.impl;

import com.navi.stockexchange.model.Order;
import com.navi.stockexchange.repository.OrderRepositoryService;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.PriorityQueue;

public class SellOrderRepositoryServiceImpl implements OrderRepositoryService<Order> {

    private PriorityQueue<Order> sellOrderQueue;

    public SellOrderRepositoryServiceImpl(Comparator<Order> comparator) {
        sellOrderQueue = new PriorityQueue<>(comparator);
    }

    @Override
    public void insertOrderInQueue(Order order) {
        sellOrderQueue.add(order);
    }

    @Override
    public Order getFirstOrderInQueue() {
        return sellOrderQueue.peek();
    }

    @Override
    public Order removeFirstOrderInQueue() {
        return sellOrderQueue.poll();
    }

    @Override
    public LinkedList<Order> getAllOrdersInQueue() {
        throw new UnsupportedOperationException("Method unsupported for SellOrderRepositoryServiceImpl.");
    }
}
