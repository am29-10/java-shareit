package ru.practicum.shareit.item.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.comment.Comment;
import ru.practicum.shareit.item.comment.CommentDto;
import ru.practicum.shareit.item.comment.CommentMapper;
import ru.practicum.shareit.item.dto.ItemBookingDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

@RestController
@Slf4j
@AllArgsConstructor
@RequestMapping("/items")
public class ItemController {

    private final ItemService itemService;

    @GetMapping
    public List<ItemBookingDto> getAll(@RequestHeader("X-Sharer-User-id") long userId,
                                       @RequestParam(defaultValue = "0") int from,
                                       @RequestParam(defaultValue = "10") int size) {
        log.info("Получен запрос GET /items");
        return itemService.readAllByUserId(userId, from, size);
    }

    @GetMapping("/{id}")
    public ItemBookingDto getItem(@RequestHeader("X-Sharer-User-id") long userId, @PathVariable Long id) {
        log.info("Получен запрос GET /items/{}", id);
        return itemService.getItemByUserId(id, userId);
    }

    @PostMapping
    public ItemDto create(@RequestHeader("X-Sharer-User-id") long userId, @RequestBody ItemDto itemDto) {
        log.info("Получен запрос POST /items");
        Item item = ItemMapper.toItem(itemDto);
        return itemService.create(item, userId);
    }

    @PatchMapping("/{id}")
    public Item update(@RequestHeader("X-Sharer-User-id") long userId, @PathVariable Long id,
                       @RequestBody ItemDto itemDto) {
        log.info("Получен запрос PUT /items");
        Item item = ItemMapper.toItem(itemDto);
        return itemService.update(id, item, userId);
    }

    @DeleteMapping("/{id}")
    public void delete(@RequestHeader("X-Sharer-User-id") long userId, @PathVariable Long id) {
        log.info("Получен запрос DELETE /items");
        itemService.delete(id, userId);
    }

    @GetMapping("/search")
    public List<ItemDto> searching(@RequestParam String text,
                                @RequestParam(defaultValue = "0") int from,
                                @RequestParam(defaultValue = "10") int size) {
        log.info("Получен запрос GET /search");
        return itemService.findItemsByText(text, from, size);
    }

    @PostMapping("/{id}/comment")
    public CommentDto addComment(@RequestHeader("X-Sharer-User-id") long userId, @PathVariable Long id,
                                 @RequestBody CommentDto commentDto) {
        log.info("Получен запрос POST /{}/comment", id);
        Comment comment = CommentMapper.toComment(commentDto);
        return itemService.createComment(id, userId, comment);
    }

}
