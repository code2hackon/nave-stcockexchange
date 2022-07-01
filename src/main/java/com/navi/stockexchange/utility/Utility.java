package com.navi.stockexchange.utility;

import com.navi.stockexchange.dto.TradeOrderDto;
import com.navi.stockexchange.enums.OrderType;
import com.navi.stockexchange.exceptions.BadRequestException;
import com.navi.stockexchange.model.Order;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Utility {

    private final static DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");
    private final static String LINE_ORDER_DATA_DELIMETER = " ";

    public static long getTimeInMillis(String timeInHourAndMinutes) {
        try {
            LocalTime localTime = LocalTime.parse(timeInHourAndMinutes, DATE_TIME_FORMATTER);
            return localTime.toSecondOfDay() * 1000L;
        } catch (Exception ex) {
            System.out.println("Error while parsing time " + timeInHourAndMinutes);
            throw new RuntimeException(ex);
        }
    }

    public static Order parseLineOrderData(String lineOrder) {
        String[] orderData = lineOrder.trim().split(LINE_ORDER_DATA_DELIMETER);
        if (orderData.length != 6)
            throw new BadRequestException("Invalid number of values in request");
        return new Order.Builder()
                .setOrderId(orderData[0])
                .setTime(Utility.getTimeInMillis(orderData[1]))
                .setStock(orderData[2])
                .setOrderType(OrderType.valueOf(orderData[3].toUpperCase(Locale.ROOT)))
                .setPrice(Double.parseDouble(orderData[4]))
                .setQuantity(Long.parseLong(orderData[5]))
                .build();
    }

    public static List<Order> readInputFileToList(String filename) {
        List<String> result;
        try (Stream<String> lines = Files.lines(Paths.get(filename))) {
            result = lines.collect(Collectors.toList());
            List<Order> orderList = new ArrayList<>();

            for (String lineOrderData : result) {
                Order order = Utility.parseLineOrderData(lineOrderData);
                orderList.add(order);
            }
            return orderList;
        } catch (IOException ex) {
            throw new RuntimeException("Error reading input file : " + filename, ex);
        }
    }

    public static String getOutput(List<TradeOrderDto> tradeOrderDtoList) {
        StringBuilder sb = new StringBuilder();
        for (TradeOrderDto tradeOrderDto : tradeOrderDtoList) {
            sb.append(tradeOrderDto.getBuyOrderId()).append(LINE_ORDER_DATA_DELIMETER);
            sb.append(String.format("%.2f", tradeOrderDto.getPrice())).append(LINE_ORDER_DATA_DELIMETER);
            sb.append(tradeOrderDto.getQuantity()).append(LINE_ORDER_DATA_DELIMETER);
            sb.append(tradeOrderDto.getSellOrderId());
            sb.append("\n");
        }
        return sb.toString();
    }
}
