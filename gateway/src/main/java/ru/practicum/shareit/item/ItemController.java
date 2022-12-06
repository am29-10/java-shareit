package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.Valid;

@Controller
@RequestMapping(path = "/items")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemController {
    private final ItemClient itemClient;

    @GetMapping
    public ResponseEntity<Object> getAll(@RequestHeader("X-Sharer-User-id") long userId,
                                         @RequestParam(defaultValue = "0") int from,
                                         @RequestParam(defaultValue = "10") int size) {
        log.info("Получен запрос GET /items");
        return itemClient.getItems(userId, from, size);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getItem(@RequestHeader("X-Sharer-User-id") long userId, @Valid @PathVariable Long id) {
        log.info("Получен запрос GET /items/{}", id);
        return itemClient.getItem(userId, id);
    }

    @PostMapping
    public ResponseEntity<Object> create(@RequestHeader("X-Sharer-User-id") long userId,
                                         @Valid @RequestBody ItemDto itemDto) {
        log.info("Получен запрос POST /items");
        return itemClient.createItem(userId, itemDto);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> update(@RequestHeader("X-Sharer-User-id") long userId, @Valid @PathVariable Long id,
                       @RequestBody ItemDto itemDto) {
        log.info("Получен запрос PUT /items");
        return itemClient.updateItem(userId, id, itemDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> delete(@RequestHeader("X-Sharer-User-id") long userId, @Valid @PathVariable Long id) {
        log.info("Получен запрос DELETE /items");
        return itemClient.deleteItem(userId, id);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searching(@RequestParam String text,
                                   @RequestParam(defaultValue = "0") int from,
                                   @RequestParam(defaultValue = "10") int size) {
        log.info("Получен запрос GET /search");
        return itemClient.searching(text, from, size);
    }

    @PostMapping("/{id}/comment")
    public ResponseEntity<Object> addComment(@RequestHeader("X-Sharer-User-id") long userId, @Valid @PathVariable Long id,
                                 @Valid @RequestBody CommentDto commentDto) {
        log.info("Получен запрос POST /{}/comment", id);
        return itemClient.addComment(userId, id, commentDto);
    }
}
