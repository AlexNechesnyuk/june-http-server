package ru.otus.java.basic.june.http.server.app;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.otus.java.basic.june.http.server.Dispatcher;

import java.math.BigDecimal;
import java.util.*;

public class ItemsRepository implements ItemsRepo{
    private List<Item> items;
    private static final Logger logger = LogManager.getLogger(ItemsRepository.class);

    public ItemsRepository() {
        this.items = new ArrayList<>(Arrays.asList(
                new Item(1L, "Milk", BigDecimal.valueOf(80)),
                new Item(2L, "Bread", BigDecimal.valueOf(38))
        ));
    }

    @Override
    public List<Item> getAll() {
        return Collections.unmodifiableList(items);
    }

    @Override
    public Item get(Long id) {
        for (Item i : items) {
            if (i.getId().equals(id)) {
                return i;
            }
        }
        return null;
    }

    @Override
    public void delete(Long id) {
        Iterator<Item> iterator = items.iterator();
        while (iterator.hasNext()) {
            Item product = iterator.next();
            if (product.getId().equals(id)) {
                iterator.remove();
                logger.debug("Удален товар: " + product);
                return;
            }
        }
    }

    @Override
    public Item create(Item item) {
        Long newId = 1L;
        for (Item i : items) {
            if (newId <= i.getId()) {
                newId = i.getId() + 1L;
            }
        }
        item.setId(newId);
        items.add(item);
        return item;
    }
}
