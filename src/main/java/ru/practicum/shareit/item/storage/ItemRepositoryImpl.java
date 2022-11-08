package ru.practicum.shareit.item.storage;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class ItemRepositoryImpl implements ItemRepository {

    private final Map<Long, Item> items;
    private static long itemId;

    public ItemRepositoryImpl() {
        items = new HashMap<>();
    }

    private long generateId() {
        return ++itemId;
    }

    @Override
    public Item create(Item item, User user) {
        item.setId(generateId());
        item.setOwner(user);
        items.put(item.getId(), item);
        return items.get(item.getId());
    }

    @Override
    public List<Item> readAll() {
        return new ArrayList<>(items.values());
    }

    @Override
    public List<Item> readAllByUserId(Long id) {
        List<Item> itemsById = new ArrayList<>();
        for (Item item : readAll()) {
            if (item.getOwner().getId() == id) {
                itemsById.add(item);
            }
        }
        return itemsById;
    }

    @Override
    public Item update(Long id, Item item, User user) {
        if (item.getName() != null) {
            items.get(id).setName(item.getName());
        }
        if (item.getDescription() != null) {
            items.get(id).setDescription(item.getDescription());
        }
        if (item.getAvailable() != null) {
            items.get(id).setAvailable(item.getAvailable());
        }
        if (user != null) {
            items.get(id).setOwner(user);
        }
        return items.get(id);
    }

    @Override
    public Item getItemById(Long id) {
        return items.get(id);
    }

    @Override
    public void delete(Long id) {
        items.remove(id);
    }

    @Override
    public List<Item> findItemsByText(String text) {
        List<Item> itemList = new ArrayList<>();
        for (Item item : readAll()) {
            String name = item.getName().toLowerCase();
            String description = item.getDescription().toLowerCase();
            if ((name.contains(text.toLowerCase()) || description.contains(text.toLowerCase()))
                    && item.getAvailable() && !text.isBlank()) {
                itemList.add(item);
            }
        }
        return itemList;
    }


}
