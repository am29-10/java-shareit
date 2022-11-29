package ru.practicum.shareit.itemTests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.item.comment.Comment;
import ru.practicum.shareit.item.comment.CommentRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
class CommentRepositoryTest {

    @Autowired
    CommentRepository commentRepository;

    @Autowired
    ItemRepository itemRepository;

    @Autowired
    UserRepository userRepository;

    Item item1;

    User user1;

    Comment comment1;

    @BeforeEach
    void beforeEach() {
        user1 = userRepository.save(User.builder()
                .id(1L)
                .name("user1")
                .email("user1@mail.ru")
                .build());
        item1 = itemRepository.save(Item.builder()
                .id(1L)
                .name("item1")
                .description("description1")
                .available(true)
                .owner(user1)
                .itemRequest(null)
                .build());
        comment1 = commentRepository.save(Comment.builder()
                .id(1L)
                .author(user1)
                .text("text")
                .item(item1)
                .created(LocalDateTime.now())
                .build());
    }

    @Test
    void findAllByItem() {
        List<Comment> comments = commentRepository.findAllByItem(item1);
        assertNotNull(comments);
        assertEquals(1, comments.size());
        assertEquals(comments.get(0), comment1);

    }
}