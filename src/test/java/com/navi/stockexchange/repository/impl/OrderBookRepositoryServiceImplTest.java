package com.navi.stockexchange.repository.impl;

import com.navi.stockexchange.exceptions.BadRequestException;
import com.navi.stockexchange.model.Order;
import com.navi.stockexchange.model.TradedOrder;
import com.navi.stockexchange.repository.OrderRepositoryService;
import com.navi.stockexchange.service.impl.BaseOrderTest;
import com.navi.stockexchange.utility.Utility;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(MockitoJUnitRunner.class)
class OrderBookRepositoryServiceImplTest extends BaseOrderTest {

    @InjectMocks
    private OrderRepositoryService<TradedOrder> orderBookRepositoryService = new OrderBookRepositoryServiceImpl();

    @BeforeEach
    void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void removeFirstOrderInQueueTestSuccess() {
        List<TradedOrder> orderList = getSampleTradedOrders();
        orderList.forEach(order -> orderBookRepositoryService.insertOrderInQueue(order));
        assertEquals(orderList.get(0), orderBookRepositoryService.removeFirstOrderInQueue());
        assertEquals(orderList.get(1), orderBookRepositoryService.removeFirstOrderInQueue());
        assertEquals(orderList.get(2), orderBookRepositoryService.removeFirstOrderInQueue());
        assertEquals(orderList.get(3), orderBookRepositoryService.removeFirstOrderInQueue());
        assertNull(orderBookRepositoryService.removeFirstOrderInQueue());
    }

    @Test
    void getFirstOrderInQueueTestSuccess() {
        List<TradedOrder> orderList = getSampleTradedOrders();
        orderBookRepositoryService.insertOrderInQueue(orderList.get(0));
        assertEquals(orderList.get(0), orderBookRepositoryService.getFirstOrderInQueue());
        orderBookRepositoryService.insertOrderInQueue(orderList.get(1));
        assertEquals(orderList.get(0), orderBookRepositoryService.getFirstOrderInQueue());
        orderBookRepositoryService.insertOrderInQueue(orderList.get(2));
        assertEquals(orderList.get(0), orderBookRepositoryService.getFirstOrderInQueue());
        orderBookRepositoryService.removeFirstOrderInQueue();
        assertEquals(orderList.get(1), orderBookRepositoryService.getFirstOrderInQueue());
    }

    @Test
    void sellOrderRepositoryServiceImplTest_WhenBuyOrderIsNull() {
        TradedOrder tradedOrder = new TradedOrder.Builder()
                .setBuyOrder(null)
                .setSellOrder(Utility.parseLineOrderData("#1 09:46 BAC sell 200.12 90"))
                .setQuantity(90L)
                .setPrice(200.12)
                .build();
        assertThrows(BadRequestException.class, () -> orderBookRepositoryService.insertOrderInQueue(tradedOrder));
    }

    @Test
    void sellOrderRepositoryServiceImplTest_WhenSellOrderIsNull() {
        TradedOrder tradedOrder = new TradedOrder.Builder()
                .setBuyOrder(Utility.parseLineOrderData("#1 09:46 BAC buy 200.12 90"))
                .setSellOrder(null)
                .setQuantity(90L)
                .setPrice(200.12)
                .build();
        Exception ex = assertThrows(BadRequestException.class, () -> orderBookRepositoryService.insertOrderInQueue(tradedOrder));
        assertEquals("Bad request for buy and sell order", ex.getMessage());
    }

    @Test
    void sellOrderRepositoryServiceImplTest_WhenQtyIsLessThanEqualZero() {
        TradedOrder tradedOrder = new TradedOrder.Builder()
                .setBuyOrder(Utility.parseLineOrderData("#1 09:46 BAC buy 200.12 90"))
                .setSellOrder(Utility.parseLineOrderData("#2 09:46 BAC sell 200.12 90"))
                .setQuantity(0L)
                .setPrice(200.12)
                .build();
        Exception ex = assertThrows(BadRequestException.class, () -> orderBookRepositoryService.insertOrderInQueue(tradedOrder));
        assertEquals("Bad request for buy and sell order", ex.getMessage());
    }

    @Test
    void sellOrderRepositoryServiceImplTest_WhenQtyNotMatchesMinOfBuyOrSell() {
        TradedOrder tradedOrder = new TradedOrder.Builder()
                .setBuyOrder(Utility.parseLineOrderData("#1 09:46 BAC buy 100.12 90"))
                .setSellOrder(Utility.parseLineOrderData("#2 09:46 BAC sell 200.12 90"))
                .setQuantity(10L)
                .setPrice(200.12)
                .build();
        Exception ex = assertThrows(BadRequestException.class, () -> orderBookRepositoryService.insertOrderInQueue(tradedOrder));
        assertEquals("Bad request for buy and sell order", ex.getMessage());
    }

    @Test
    void sellOrderRepositoryServiceImplTest_WhenTradePriceNotMatchesSellPrice() {
        TradedOrder tradedOrder = new TradedOrder.Builder()
                .setBuyOrder(Utility.parseLineOrderData("#1 09:46 BAC buy 200.12 90"))
                .setSellOrder(Utility.parseLineOrderData("#2 09:46 BAC sell 200.12 90"))
                .setQuantity(90L)
                .setPrice(100.00)
                .build();
        Exception ex = assertThrows(BadRequestException.class, () -> orderBookRepositoryService.insertOrderInQueue(tradedOrder));
        assertEquals("Bad request for buy and sell order", ex.getMessage());
    }

    @Test
    void getAllOrdersInQueueTest() {
        List<TradedOrder> orderList = getSampleTradedOrders();
        orderList.forEach(order -> orderBookRepositoryService.insertOrderInQueue(order));
        assertEquals(4, orderBookRepositoryService.getAllOrdersInQueue().size());
        IntStream.range(0, 4).forEach(index -> assertEquals(orderList.get(index), orderBookRepositoryService.getAllOrdersInQueue().get(index)));
    }

    private List<TradedOrder> getSampleTradedOrders() {
        Order buyOrder1 = Utility.parseLineOrderData("#0 09:47 BAC buy 240.12 70");
        Order sellOrder1 = Utility.parseLineOrderData("#1 09:46 BAC sell 200.12 90");
        Order buyOrder2 = Utility.parseLineOrderData("#2 09:48 BAC buy 240.12 40");
        Order sellOrder2 = Utility.parseLineOrderData("#3 09:49 BAC sell 190.00 60");
        return Arrays.asList(
                new TradedOrder(buyOrder1, sellOrder1, 200.12, 70L),
                new TradedOrder(buyOrder2, sellOrder2, 190.00, 40L),
                new TradedOrder(buyOrder1, sellOrder2, 190.00, 60L),
                new TradedOrder(buyOrder2, sellOrder1, 200.12, 40L)
        );
    }

}