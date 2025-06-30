package ru.otus.java.basic.june.http.server.app;

import java.util.List;

public interface ItemsRepo {
    public List<Item> getAll();

    public Item get(Long id);

    public Item create(Item item);

    public void delete(Long id);
}