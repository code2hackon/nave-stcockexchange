package com.navi.stockexchange.model;

import com.navi.stockexchange.enums.OrderType;

import java.util.Objects;

public class Order {
    private String orderId;
    private Long time;
    private String stock;
    private OrderType orderType;
    private Double price;
    private Long quantity;

    public Order() {
    }

    public Order(String orderId, Long time, String stock, OrderType orderType, Double price, Long quantity) {
        this.orderId = orderId;
        this.time = time;
        this.stock = stock;
        this.orderType = orderType;
        this.price = price;
        this.quantity = quantity;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Order order = (Order) o;
        return Objects.equals(orderId, order.orderId) && Objects.equals(time, order.time) && Objects.equals(stock, order.stock) && orderType == order.orderType && Objects.equals(price, order.price) && Objects.equals(quantity, order.quantity);
    }

    @Override
    public int hashCode() {
        return Objects.hash(orderId, time, stock, orderType, price, quantity);
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    public String getStock() {
        return stock;
    }

    public void setStock(String stock) {
        this.stock = stock;
    }

    public OrderType getOrderType() {
        return orderType;
    }

    public void setOrderType(OrderType orderType) {
        this.orderType = orderType;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Long getQuantity() {
        return quantity;
    }

    public void setQuantity(Long quantity) {
        this.quantity = quantity;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String orderId;
        private Long time;
        private String stock;
        private OrderType orderType;
        private Double price;
        private Long quantity;

        public Builder setOrderId(String orderId) {
            this.orderId = orderId;
            return this;
        }

        public Builder setTime(Long time) {
            this.time = time;
            return this;
        }

        public Builder setStock(String stock) {
            this.stock = stock;
            return this;
        }

        public Builder setOrderType(OrderType orderType) {
            this.orderType = orderType;
            return this;
        }

        public Builder setPrice(Double price) {
            this.price = price;
            return this;
        }

        public Builder setQuantity(Long quantity) {
            this.quantity = quantity;
            return this;
        }

        public Order build() {
            return new Order(orderId, time, stock, orderType, price, quantity);
        }

    }

    @Override
    public String toString() {
        return "Order{" +
                "orderId='" + orderId + '\'' +
                ", time=" + time +
                ", stock='" + stock + '\'' +
                ", orderType=" + orderType +
                ", price=" + price +
                ", quantity=" + quantity +
                '}';
    }
}
