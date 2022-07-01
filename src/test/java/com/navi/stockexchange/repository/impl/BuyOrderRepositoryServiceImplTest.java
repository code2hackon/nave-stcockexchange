package com.navi.stockexchange.repository.impl;

import com.navi.stockexchange.model.Order;
import com.navi.stockexchange.repository.OrderRepositoryService;
import com.navi.stockexchange.service.comparators.OrderComparatorByDescendingPriceAndAscendingTime;
import com.navi.stockexchange.service.impl.BaseOrderTest;
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
class BuyOrderRepositoryServiceImplTest extends BaseOrderTest {

    @InjectMocks
    private OrderRepositoryService<Order> buyOrderRepositoryService = new BuyOrderRepositoryServiceImpl(
            new OrderComparatorByDescendingPriceAndAscendingTime());

    @BeforeEach
    void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void removeFirstOrderInQueueTestSuccess() {
        List<Order> orderList = mapLineOrdersToObjectList(getSampleLineOrderList());
        orderList.forEach(order -> buyOrderRepositoryService.insertOrderInQueue(order));
        assertEquals(orderList.get(0), buyOrderRepositoryService.removeFirstOrderInQueue());
        assertEquals(orderList.get(1), buyOrderRepositoryService.removeFirstOrderInQueue());
        assertEquals(orderList.get(3), buyOrderRepositoryService.removeFirstOrderInQueue());
        assertEquals(orderList.get(4), buyOrderRepositoryService.removeFirstOrderInQueue());
        assertEquals(orderList.get(2), buyOrderRepositoryService.removeFirstOrderInQueue());
        assertNull(buyOrderRepositoryService.removeFirstOrderInQueue());
    }

    @Test
    void getFirstOrderInQueueTestSuccess() {
        List<Order> orderList = mapLineOrdersToObjectList(getSampleLineOrderList());
        buyOrderRepositoryService.insertOrderInQueue(orderList.get(4));
        assertEquals(orderList.get(4), buyOrderRepositoryService.getFirstOrderInQueue());
        buyOrderRepositoryService.insertOrderInQueue(orderList.get(0));
        assertEquals(orderList.get(0), buyOrderRepositoryService.getFirstOrderInQueue());
        buyOrderRepositoryService.insertOrderInQueue(orderList.get(3));
        assertEquals(orderList.get(0), buyOrderRepositoryService.getFirstOrderInQueue());
    }

    @Test
    void getAllOrdersInQueueTest() {
        assertThrows(UnsupportedOperationException.class, () -> buyOrderRepositoryService.getAllOrdersInQueue());
    }

    private List<String> getSampleLineOrderList() {
        return Arrays.asList(
                "#0 09:45 BAC buy 240.12 90",
                "#1 09:46 BAC buy 240.12 90",
                "#2 09:46 BAC buy 200.00 90",
                "#3 09:47 BAC buy 220.12 90",
                "#4 09:48 BAC buy 220.12 90");
    }
}