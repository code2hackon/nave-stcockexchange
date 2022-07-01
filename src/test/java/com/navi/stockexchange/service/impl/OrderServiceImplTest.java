package com.navi.stockexchange.service.impl;

import com.navi.stockexchange.exceptions.BadRequestException;
import com.navi.stockexchange.model.Order;
import com.navi.stockexchange.repository.OrderRepositoryService;
import com.navi.stockexchange.repository.impl.BuyOrderRepositoryServiceImpl;
import com.navi.stockexchange.repository.impl.SellOrderRepositoryServiceImpl;
import com.navi.stockexchange.service.OrderService;
import com.navi.stockexchange.utility.Utility;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
class OrderServiceImplTest extends BaseOrderTest {

    @InjectMocks
    private OrderService orderService;
    private OrderRepositoryService<Order> buyOrderRepositoryService;
    private OrderRepositoryService<Order> sellOrderRepositoryService;

    @BeforeEach
    void setup() {
        orderService = OrderServiceImpl.getInstance();
        buyOrderRepositoryService = mock(BuyOrderRepositoryServiceImpl.class);
        sellOrderRepositoryService = mock(SellOrderRepositoryServiceImpl.class);
        MockitoAnnotations.initMocks(this);
    }

    @AfterEach
    void cleanup() {
        orderService.cleanAllOrders();
    }

    @Test
    void placeOrderSuccess() {
        Order sellOrder1 = Utility.parseLineOrderData("#1 09:45 BAC sell 238.12 100");
        Order sellOrder2 = Utility.parseLineOrderData("#2 09:46 BAC sell 237.45 20");
        Order buyOrder1 = Utility.parseLineOrderData("#3 09:47 BAC buy 238.20 110");
        Order updatedQtyBuyOrder1 = Utility.parseLineOrderData("#3 09:47 BAC buy 238.20 90");
        Order updatedQtySellOrder1 = Utility.parseLineOrderData("#1 09:45 BAC sell 238.12 10");
        when(buyOrderRepositoryService.getFirstOrderInQueue()).thenReturn(buyOrder1, updatedQtyBuyOrder1, null);
        when(buyOrderRepositoryService.removeFirstOrderInQueue()).thenReturn(buyOrder1, updatedQtyBuyOrder1, null);
        when(sellOrderRepositoryService.getFirstOrderInQueue()).thenReturn(sellOrder2, sellOrder1, updatedQtySellOrder1);
        when(sellOrderRepositoryService.removeFirstOrderInQueue()).thenReturn(sellOrder2, sellOrder1, updatedQtySellOrder1);
        orderService.placeOrder(sellOrder1);
        orderService.placeOrder(sellOrder2);
        orderService.placeOrder(buyOrder1);
        assertEquals(2, orderService.getTradedOrders().size());
        validateTradedOrder("#3", 237.45, 20L, "#2", orderService.getTradedOrders().get(0));
        validateTradedOrder("#3", 238.12, 90L, "#1", orderService.getTradedOrders().get(1));
    }

    @Test
    void checkAndExecuteOrderIfApplicableForTradeTest() {
        Order buyOrder1 = Utility.parseLineOrderData("#1 09:47 BAC buy 238.20 110");
        Order buyOrder2 = Utility.parseLineOrderData("#2 09:47 BAC buy 200.00 110");
        Order sellOrder1 = Utility.parseLineOrderData("#3 09:45 BAC sell 238.10 100");
        when(buyOrderRepositoryService.getFirstOrderInQueue()).thenReturn(buyOrder1, buyOrder2, null);
        when(buyOrderRepositoryService.removeFirstOrderInQueue()).thenReturn(buyOrder1, buyOrder2, null);
        when(sellOrderRepositoryService.getFirstOrderInQueue()).thenReturn(sellOrder1, null);
        when(sellOrderRepositoryService.removeFirstOrderInQueue()).thenReturn(sellOrder1, null);
        orderService.checkAndExecuteOrderIfApplicableForTrade();
        assertEquals(1, orderService.getTradedOrders().size());
        validateTradedOrder("#1", 238.10, 100L, "#3", orderService.getTradedOrders().get(0));
    }


    @Test
    void checkAndExecuteOrderIfApplicableForTradeTest_WhenNoOrders() {
        when(buyOrderRepositoryService.getFirstOrderInQueue()).thenReturn(null);
        when(sellOrderRepositoryService.getFirstOrderInQueue()).thenReturn(null);
        orderService.checkAndExecuteOrderIfApplicableForTrade();
        assertEquals(0, orderService.getTradedOrders().size());
    }

    @Test
    void checkAndExecuteOrderIfApplicableForTradeTest_WhenStockAreDifferent() {
        Order buyOrder = Utility.parseLineOrderData("#1 09:47 BAC buy 238.20 110");
        Order sellOrder = Utility.parseLineOrderData("#2 09:45 ABC sell 238.10 100");
        when(buyOrderRepositoryService.getFirstOrderInQueue()).thenReturn(buyOrder, null);
        when(buyOrderRepositoryService.removeFirstOrderInQueue()).thenReturn(buyOrder, null);
        when(sellOrderRepositoryService.getFirstOrderInQueue()).thenReturn(sellOrder, null);
        when(sellOrderRepositoryService.removeFirstOrderInQueue()).thenReturn(sellOrder, null);
        orderService.checkAndExecuteOrderIfApplicableForTrade();
        assertEquals(0, orderService.getTradedOrders().size());
    }

    @Test
    void checkAndExecuteOrderIfApplicableForTradeTest_WhenOrderAreDifferentInPeekAndPoll() {
        Order buyOrder = Utility.parseLineOrderData("#1 09:47 BAC buy 238.20 110");
        Order buyOrder2 = Utility.parseLineOrderData("#2 09:47 BAC buy 230.00 110");
        Order sellOrder = Utility.parseLineOrderData("#3 09:45 BAC sell 238.10 100");
        when(buyOrderRepositoryService.getFirstOrderInQueue()).thenReturn(buyOrder, null);
        when(buyOrderRepositoryService.removeFirstOrderInQueue()).thenReturn(buyOrder2, null);
        when(sellOrderRepositoryService.getFirstOrderInQueue()).thenReturn(sellOrder, null);
        when(sellOrderRepositoryService.removeFirstOrderInQueue()).thenReturn(sellOrder, null);
        assertThrows(RuntimeException.class, () -> orderService.checkAndExecuteOrderIfApplicableForTrade(), "Expected Buy/Sell order mismatched");
        assertEquals(0, orderService.getTradedOrders().size());
    }

    @Test
    void validateNullTest() {
        Order order = Utility.parseLineOrderData("#0 09:45 BAC sell 240.12 90");
        order.setOrderType(null);
        Exception ex = assertThrows(BadRequestException.class, () -> orderService.placeOrder(order));
        assertEquals("Order contains null value", ex.getMessage());
    }

    @Test
    void validateDuplicateOrderTest() {
        Order order1 = Utility.parseLineOrderData("#0 09:45 BAC sell 240.12 90");
        Order order2 = Utility.parseLineOrderData("#0 09:45 BAC sell 240.12 90");
        Exception ex = assertThrows(BadRequestException.class, () -> {
            orderService.placeOrder(order1);
            orderService.placeOrder(order2);
        });
        assertEquals("Order already exist", ex.getMessage());
    }
}