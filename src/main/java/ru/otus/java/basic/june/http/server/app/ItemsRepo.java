package ru.otus.java.basic.june.http.server.app;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public interface ItemsRepo {
    public List<Item> getAll();
    public Item get(Long id);
    public Item create(Item item);
}