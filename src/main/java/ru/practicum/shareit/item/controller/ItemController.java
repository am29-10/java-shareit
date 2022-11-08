package ru.practicum.shareit.item.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.Valid;
import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@Slf4j
@RequestMapping("/items")
public class ItemController {

    private final ItemService itemService;
    private final UserService userService;

    @Autowired
    public ItemController(ItemService itemService, UserService userService) {
        this.itemService = itemService;
        this.userService = userService;
    }

    @GetMapping
    public List<Item> getAll(@RequestHeader("X-Sharer-User-id") long userId) {
        log.info("Получен запрос GET /items");
        return itemService.readAllByUserId(userId);
    }

    @GetMapping("/{id}")
    public Item get(@RequestHeader("X-Sharer-User-id") long userId, @Valid @PathVariable Long id) {
        log.info("Получен запрос GET /items/{}", id);
        return itemService.getItemById(id);
    }

    @PostMapping
    public Item create(@RequestHeader("X-Sharer-User-id") long userId, @Valid @RequestBody ItemDto itemDto) {
        log.info("Получен запрос POST /items");
        Item item = ItemMapper.toItem(itemDto);
        return itemService.create(item, userId);
    }

    @PatchMapping("/{id}")
    public Item update(@RequestHeader("X-Sharer-User-id") long userId, @Valid @PathVariable Long id,
                       @RequestBody ItemDto itemDto) {
        log.info("Получен запрос PUT /items");
        Item item = ItemMapper.toItem(itemDto);
        return itemService.update(id, item, userId);
    }

    @DeleteMapping("/{id}")
    public void delete(@RequestHeader("X-Sharer-User-id") long userId, @Valid @PathVariable Long id) {
        log.info("Получен запрос DELETE /items");
        itemService.delete(id, userId);
    }

    @GetMapping("/search")
    private List<Item> searching(@RequestParam String text) {
        return itemService.findItemsByText(text);
    }
}
