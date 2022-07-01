package com.navi.stockexchange;

import com.navi.stockexchange.model.Order;
import com.navi.stockexchange.service.OrderService;
import com.navi.stockexchange.service.impl.OrderServiceImpl;
import com.navi.stockexchange.utility.Utility;

import java.util.List;

public class StockExchangeApplication {

    private static OrderService orderService = OrderServiceImpl.getInstance();

    public static void main(String[] args) {
        try {
            if (args.length == 0)
                throw new RuntimeException("Input filename is missing in arguments.");
            String filename = args[0];
            List<Order> orderList = Utility.readInputFileToList(filename);
            orderList.forEach(orderService::placeOrder);
            String output = Utility.getOutput(orderService.getTradedOrders());
            System.out.println(output);
        } catch (Exception ex) {
            System.out.println("Exception occured, " + ex);
        }
    }
}
