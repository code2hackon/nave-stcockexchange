package com.navi.stockexchange.service.impl;

import com.navi.stockexchange.dto.TradeOrderDto;
import com.navi.stockexchange.enums.OrderType;
import com.navi.stockexchange.exceptions.BadRequestException;
import com.navi.stockexchange.model.Order;
import com.navi.stockexchange.model.TradedOrder;
import com.navi.stockexchange.repository.OrderRepositoryService;
import com.navi.stockexchange.repository.impl.BuyOrderRepositoryServiceImpl;
import com.navi.stockexchange.repository.impl.OrderBookRepositoryServiceImpl;
import com.navi.stockexchange.repository.impl.SellOrderRepositoryServiceImpl;
import com.navi.stockexchange.service.OrderService;
import com.navi.stockexchange.service.comparators.OrderComparatorByAscendingPriceAndAscendingTime;
import com.navi.stockexchange.service.comparators.OrderComparatorByDescendingPriceAndAscendingTime;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class OrderServiceImpl implements OrderService {

    private OrderRepositoryService<TradedOrder> orderBookRepositoryService;
    private OrderRepositoryService<Order> buyOrderRepositoryService;
    private OrderRepositoryService<Order> sellOrderRepositoryService;
    private Set<Order> orderHistorySet;

    private OrderServiceImpl() {
        initialize();
    }

    private void initialize() {
        orderBookRepositoryService = new OrderBookRepositoryServiceImpl();
        buyOrderRepositoryService = new BuyOrderRepositoryServiceImpl(new OrderComparatorByDescendingPriceAndAscendingTime());
        sellOrderRepositoryService = new SellOrderRepositoryServiceImpl(new OrderComparatorByAscendingPriceAndAscendingTime());
        orderHistorySet = new HashSet<Order>();
    }

    private static class OrderServiceImplSingleton {
        private static final OrderServiceImpl INSTANCE = new OrderServiceImpl();
    }

    public static OrderServiceImpl getInstance() {
        return OrderServiceImplSingleton.INSTANCE;
    }

    @Override
    public void placeOrder(Order order) {
        validateAndProcessOrder(order);
    }

    @Override
    public List<TradeOrderDto> getTradedOrders() {
        return orderBookRepositoryService.getAllOrdersInQueue().stream().map(dto -> new TradeOrderDto.Builder()
                        .setBuyOrderId(dto.getBuyOrder().getOrderId())
                        .setSellOrderId(dto.getSellOrder().getOrderId())
                        .setPrice(dto.getPrice())
                        .setQuantity(dto.getQuantity())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    public void cleanAllOrders() {
        initialize();
    }

    @Override
    public void checkAndExecuteOrderIfApplicableForTrade() {
        while (true) {
            Order nextBuyOrder = buyOrderRepositoryService.getFirstOrderInQueue();
            Order nextSellOrder = sellOrderRepositoryService.getFirstOrderInQueue();

            if (nextBuyOrder == null || nextSellOrder == null || !nextSellOrder.getStock().equals(nextBuyOrder.getStock()))
                break;
            if (nextBuyOrder.getPrice() < nextSellOrder.getPrice())
                break;
            executeTradeOrder(nextBuyOrder, nextSellOrder);
        }
    }

    private void executeTradeOrder(Order buyOrder, Order sellOrder) {
        Order polledBuyOrder = buyOrderRepositoryService.removeFirstOrderInQueue();
        Order polledSellOrder = sellOrderRepositoryService.removeFirstOrderInQueue();

        if (!buyOrder.equals(polledBuyOrder) || !sellOrder.equals(polledSellOrder))
            throw new RuntimeException("Expected Buy/Sell order mismatched");

        updateTradeOrder(buyOrder, sellOrder);
    }

    private void updateTradeOrder(Order buyOrder, Order sellOrder) {
        long orderQty = Math.min(buyOrder.getQuantity(), sellOrder.getQuantity());
        double orderPrice = sellOrder.getPrice();

        TradedOrder tradedOrder = new TradedOrder.Builder()
                .setBuyOrder(buyOrder)
                .setSellOrder(sellOrder)
                .setQuantity(orderQty)
                .setPrice(orderPrice).build();

        orderBookRepositoryService.insertOrderInQueue(tradedOrder);
        updateOrder(buyOrder, tradedOrder);
        updateOrder(sellOrder, tradedOrder);
    }

    private void updateOrder(Order order, TradedOrder tradedOrder) {
        if (order.getQuantity() > tradedOrder.getQuantity()) {
            long remainingOrderQty = order.getQuantity() - tradedOrder.getQuantity();
            order.setQuantity(remainingOrderQty);
            if (order.getOrderType().equals(OrderType.BUY))
                buyOrderRepositoryService.insertOrderInQueue(order);
            else
                sellOrderRepositoryService.insertOrderInQueue(order);
        }
    }

    private void validateAndProcessOrder(Order order) {
        if (order.getOrderId() == null || order.getQuantity() == null || order.getOrderType() == null
                || order.getStock() == null || order.getPrice() == null || order.getTime() == null)
            throw new BadRequestException("Order contains null value");

        if (!order.getOrderType().equals(OrderType.SELL) && !order.getOrderType().equals(OrderType.BUY))
            throw new BadRequestException("Order type BUY/SELL expected");

        if (orderHistorySet.contains(order))
            throw new BadRequestException("Order already exist");

        processOrder(order);
    }

    private void processOrder(Order order) {
        switch (order.getOrderType()) {
            case BUY: {
                buyOrderRepositoryService.insertOrderInQueue(order);
                checkAndExecuteOrderIfApplicableForTrade();
                break;
            }
            case SELL: {
                sellOrderRepositoryService.insertOrderInQueue(order);
                checkAndExecuteOrderIfApplicableForTrade();
                break;
            }
            default:
                throw new UnsupportedOperationException("Order not supported of type: " + order.getOrderType());
        }
        orderHistorySet.add(order);
    }

}
