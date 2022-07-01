package com.navi.stockexchange.utility;


import com.navi.stockexchange.exceptions.BadRequestException;
import com.navi.stockexchange.model.Order;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class UtilityTest {

    @Test
    void getTimeInMillisTest() {
        assertEquals(0, Utility.getTimeInMillis("00:00"));
        assertEquals(60 * 1000, Utility.getTimeInMillis("00:01"));
        assertEquals(60 * 60 * 1000, Utility.getTimeInMillis("01:00"));
        assertEquals(13 * 60 * 60 * 1000 + 5 * 60 * 1000, Utility.getTimeInMillis("13:05"));
    }

    @Test
    void getTimeInMillisTestWhenInvalidInput() {
        Assertions.assertThrows(RuntimeException.class, () -> Utility.getTimeInMillis("27:00"));
    }

    @Test
    void parseLineOrderDataSuccess() {
        String lineOrder = "#1 13:05 BAC sell 240.12 100";
        Order order = Utility.parseLineOrderData(lineOrder);
        assertEquals("#1", order.getOrderId());
        assertEquals(13 * 60 * 60 * 1000 + 5 * 60 * 1000, order.getTime().longValue());
        assertEquals("BAC", order.getStock());
        assertEquals("SELL", order.getOrderType().value);
        assertEquals(240.12, order.getPrice().doubleValue());
        assertEquals(100, order.getQuantity().longValue());
    }

    @Test
    void parseLineOrderDataTestSuccess_WhenInvalidRequestLine() {
        String lineOrder = "#1 13:05 BAC 240.12 100";
        Exception ex = assertThrows(BadRequestException.class, () -> Utility.parseLineOrderData(lineOrder));
        assertEquals("Invalid number of values in request", ex.getMessage());
    }

    @Test
    void readInputFileToListTestSuccess() {
        String filename = ".\\src\\main\\resources\\test.txt";
        List<Order> orderList = Utility.readInputFileToList(filename);
        assertEquals(6, orderList.size());
        assertEquals(Utility.parseLineOrderData("#1 09:45 BAC sell 240.12 100"), orderList.get(0));
        assertEquals(Utility.parseLineOrderData("#2 09:46 BAC sell 237.45 90"), orderList.get(1));
        assertEquals(Utility.parseLineOrderData("#3 09:47 BAC buy 238.10 110"), orderList.get(2));
        assertEquals(Utility.parseLineOrderData("#4 09:48 BAC buy 237.80 10"), orderList.get(3));
        assertEquals(Utility.parseLineOrderData("#5 09:49 BAC buy 237.80 40"), orderList.get(4));
        assertEquals(Utility.parseLineOrderData("#6 09:50 BAC sell 236.00 50"), orderList.get(5));
    }

    @Test
    void readInputFileToListTestSuccessWhenInvalidFile() {
        String filename = ".\\src\\main\\resources\\random.txt";
        Exception ex = assertThrows(RuntimeException.class, () -> Utility.readInputFileToList(filename));
        assertEquals("Error reading input file : " + filename, ex.getMessage());
    }
}