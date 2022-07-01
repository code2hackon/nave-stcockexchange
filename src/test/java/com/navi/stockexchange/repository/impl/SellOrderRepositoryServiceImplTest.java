package com.navi.stockexchange.repository.impl;

import com.navi.stockexchange.model.Order;
import com.navi.stockexchange.repository.OrderRepositoryService;
import com.navi.stockexchange.service.comparators.OrderComparatorByAscendingPriceAndAscendingTime;
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

import static org.junit.jupiter.api.Assertions.*;

@RunWith(MockitoJUnitRunner.class)
class SellOrderRepositoryServiceImplTest extends BaseOrderTest {

    @InjectMocks
    private OrderRepositoryService<Order> sellOrderRepositoryService = new SellOrderRepositoryServiceImpl(
            new OrderComparatorByAscendingPriceAndAscendingTime());

    @BeforeEach
    void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void removeFirstOrderInQueueTestSuccess() {
        List<Order> orderList = mapLineOrdersToObjectList(getSampleLineOrderList());
        orderList.forEach(order -> sellOrderRepositoryService.insertOrderInQueue(order));
        assertEquals(orderList.get(4), sellOrderRepositoryService.removeFirstOrderInQueue());
        assertEquals(orderList.get(2), sellOrderRepositoryService.removeFirstOrderInQueue());
        assertEquals(orderList.get(3), sellOrderRepositoryService.removeFirstOrderInQueue());
        assertEquals(orderList.get(1), sellOrderRepositoryService.removeFirstOrderInQueue());
        assertEquals(orderList.get(0), sellOrderRepositoryService.removeFirstOrderInQueue());
        assertNull(sellOrderRepositoryService.removeFirstOrderInQueue());
    }

    @Test
    void getFirstOrderInQueueTestSuccess() {
        List<Order> orderList = mapLineOrdersToObjectList(getSampleLineOrderList());
        sellOrderRepositoryService.insertOrderInQueue(orderList.get(4));
        assertEquals(orderList.get(4), sellOrderRepositoryService.getFirstOrderInQueue());
        sellOrderRepositoryService.insertOrderInQueue(orderList.get(0));
        assertEquals(orderList.get(4), sellOrderRepositoryService.getFirstOrderInQueue());
        sellOrderRepositoryService.insertOrderInQueue(orderList.get(3));
        assertEquals(orderList.get(4), sellOrderRepositoryService.getFirstOrderInQueue());
    }

    @Test
    void getAllOrdersInQueueTest() {
        Order o1 = Utility.parseLineOrderData("#0 09:46 BAC sell 240.12 90");
        Order o2 = Utility.parseLineOrderData("#0 09:46 BAC sell 240.12 90");
        System.out.println(o1.equals(o2));
        assertThrows(UnsupportedOperationException.class, () -> sellOrderRepositoryService.getAllOrdersInQueue());
    }

    private List<String> getSampleLineOrderList() {
        return Arrays.asList(
                "#0 09:46 BAC sell 240.12 90",
                "#1 09:45 BAC sell 240.12 90",
                "#2 09:46 BAC sell 200.00 90",
                "#3 09:47 BAC sell 220.12 90",
                "#4 09:48 BAC sell 120.10 90");
    }
}