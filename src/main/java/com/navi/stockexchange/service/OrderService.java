package com.navi.stockexchange.service;

import com.navi.stockexchange.dto.TradeOrderDto;
import com.navi.stockexchange.model.Order;

import java.util.List;

public interface OrderService {

    void placeOrder(Order order);

    void checkAndExecuteOrderIfApplicableForTrade();

    List<TradeOrderDto> getTradedOrders();

    void cleanAllOrders();
}
