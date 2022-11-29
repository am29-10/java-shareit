package ru.practicum.shareit.item.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.item.comment.Comment;
import ru.practicum.shareit.item.comment.CommentDto;
import ru.practicum.shareit.item.dto.ItemBookingDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {

    ItemDto create(Item item, Long userId);

    List<Item> readAll();

    List<ItemBookingDto> readAllByUserId(Long id, Pageable pageable);

    Item update(Long id, Item item, Long userId);

    Item getItemById(Long id);

    ItemBookingDto getItemByUserId(Long id, Long userId);

    void delete(Long id, Long userId);

    List<ItemDto> findItemsByText(String text, Pageable pageable);

    CommentDto createComment(Long itemId, Long userId, Comment comment);
}
