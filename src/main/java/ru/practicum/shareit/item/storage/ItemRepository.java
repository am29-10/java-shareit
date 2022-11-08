package ru.practicum.shareit.item.storage;

import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface ItemRepository {

    Item create(Item item, User user);

    List<Item> readAll();

    List<Item> readAllByUserId(Long id);

    Item update(Long id, Item item, User user);

    Item getItemById(Long id);

    void delete(Long id);

    List<Item> findItemsByText(String text);
}
