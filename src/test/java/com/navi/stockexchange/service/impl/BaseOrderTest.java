package com.navi.stockexchange.service.impl;

import com.navi.stockexchange.dto.TradeOrderDto;
import com.navi.stockexchange.model.Order;
import com.navi.stockexchange.utility.Utility;

import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BaseOrderTest {

    public List<Order> mapLineOrdersToObjectList(List<String> lineOrderList) {
        return lineOrderList.stream().map(Utility::parseLineOrderData)
                .collect(Collectors.toList());
    }

    protected void validateTradedOrder(String buyOrderId, Double sellPrice, Long quantity, String sellOrderId, TradeOrderDto tradeOrderDto) {
        assertEquals(buyOrderId, tradeOrderDto.getBuyOrderId());
        assertEquals(0, Double.compare(sellPrice, tradeOrderDto.getPrice()));
        assertEquals(quantity, tradeOrderDto.getQuantity());
        assertEquals(sellOrderId, tradeOrderDto.getSellOrderId());
    }


}
