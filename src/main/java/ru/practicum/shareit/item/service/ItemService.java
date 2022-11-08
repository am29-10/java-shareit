package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {

    Item create(Item item, Long userId);

    List<Item> readAll();

    List<Item> readAllByUserId(Long id);

    Item update(Long id, Item item, Long userId);

    Item getItemById(Long id);

    void delete(Long id, Long userId);

    List<Item> findItemsByText(String text);
}
