package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.comment.Comment;
import ru.practicum.shareit.item.comment.CommentDto;
import ru.practicum.shareit.item.dto.ItemBookingDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {

    Item create(Item item, Long userId);

    List<Item> readAll();

    List<ItemBookingDto> readAllByUserId(Long id);

    Item update(Long id, Item item, Long userId);

    Item getItemById(Long id);

    ItemBookingDto getItemByUserId(Long id, Long userId);

    void delete(Long id, Long userId);

    List<Item> findItemsByText(String text);

    CommentDto createComment(Long itemId, Long userId, Comment comment);
}
