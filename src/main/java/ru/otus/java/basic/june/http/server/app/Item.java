package ru.otus.java.basic.june.http.server.app;

import java.math.BigDecimal;

public class Item {
    private Long id;
    private Long stock;

    public Long getStock() {
        return stock;
    }

    public void setStock(Long stock) {
        this.stock = stock;
    }

    private String title;
    private BigDecimal price;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Item() {
    }

    public Item(Long id, String title, BigDecimal price) {
        this.id = id;
        this.title = title;
        this.price = price;
        this.stock = 0L;
    }
}
