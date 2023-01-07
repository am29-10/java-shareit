package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.comment.Comment;
import ru.practicum.shareit.item.comment.CommentDto;
import ru.practicum.shareit.item.dto.ItemBookingDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {

    ItemDto create(Item item, Long userId);

    List<Item> readAll(Integer from, Integer size);

    List<ItemBookingDto> readAllByUserId(Long id, Integer from, Integer size);

    Item update(Long id, Item item, Long userId);

    Item getItemById(Long id);

    ItemBookingDto getItemByUserId(Long id, Long userId);

    void delete(Long id, Long userId);

    List<ItemDto> findItemsByText(String text, Integer from, Integer size);

    CommentDto createComment(Long itemId, Long userId, Comment comment);
}
