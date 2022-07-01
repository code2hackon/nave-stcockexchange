package com.navi.stockexchange.repository.impl;

import com.navi.stockexchange.enums.OrderType;
import com.navi.stockexchange.exceptions.BadRequestException;
import com.navi.stockexchange.model.Order;
import com.navi.stockexchange.model.TradedOrder;
import com.navi.stockexchange.repository.OrderRepositoryService;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class OrderBookRepositoryServiceImpl implements OrderRepositoryService<TradedOrder> {

    private Queue<TradedOrder> orderBookQueue;

    public OrderBookRepositoryServiceImpl() {
        orderBookQueue = new LinkedList<TradedOrder>();
    }

    @Override
    public void insertOrderInQueue(TradedOrder tradedOrder) {
        validate(tradedOrder);
        orderBookQueue.add(tradedOrder);
    }

    @Override
    public TradedOrder getFirstOrderInQueue() {
        return orderBookQueue.peek();
    }

    @Override
    public TradedOrder removeFirstOrderInQueue() {
        return orderBookQueue.poll();
    }

    /**
     * @return A cloned list of original list.
     */
    @Override
    public List<TradedOrder> getAllOrdersInQueue() {
        return new LinkedList<>(orderBookQueue);
    }

    private void validate(TradedOrder tradedOrder) {
        Order buyOrder = tradedOrder.getBuyOrder();
        Order sellOder = tradedOrder.getSellOrder();

        if (buyOrder == null || sellOder == null || tradedOrder.getQuantity() <= 0L || tradedOrder.getPrice() <= 0.0)
            throw new BadRequestException("Bad request for buy and sell order");

        if (tradedOrder.getQuantity() != Math.min(buyOrder.getQuantity(), sellOder.getQuantity()))
            throw new BadRequestException("Bad request for buy and sell order");

        if (!buyOrder.getOrderType().equals(OrderType.BUY) || !sellOder.getOrderType().equals(OrderType.SELL)
                || sellOder.getPrice().compareTo(tradedOrder.getPrice()) != 0 || buyOrder.getOrderId().equals(sellOder.getOrderId()))
            throw new BadRequestException("Bad request for buy and sell order");
    }
}
