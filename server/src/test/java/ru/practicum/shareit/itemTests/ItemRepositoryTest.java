package ru.practicum.shareit.itemTests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class ItemRepositoryTest {

    @Autowired
    UserRepository userRepository;
    @Autowired
    ItemRepository itemRepository;
    @Autowired
    ItemRequestRepository requestRepository;

    private User user2;
    private Item item1;
    private Item item2;

    @BeforeEach
    void beforeEach() {
        User user1 = userRepository.save(User.builder()
                .id(1L)
                .name("user1")
                .email("user1@mail.ru")
                .build());
        user2 = userRepository.save(User.builder()
                .id(2L)
                .name("user2")
                .email("user2@mail.ru")
                .build());
        ItemRequest request = requestRepository.save(ItemRequest.builder()
                .id(1L)
                .description("description")
                .requestor(user1)
                .created(LocalDateTime.now())
                .build());
        item1 = itemRepository.save(Item.builder()
                .id(1L)
                .name("item1")
                .description("description1")
                .available(true)
                .owner(user1)
                .itemRequest(request)
                .build());
        item2 = itemRepository.save(Item.builder()
                .id(2L)
                .name("item2")
                .description("description2")
                .available(true)
                .owner(user2)
                .itemRequest(request)
                .build());
    }

    @Test
    void findByNameOrDescriptionContainingIgnoreCaseAndAvailableIsTrue() {
        List<Item> items = itemRepository.findByNameOrDescriptionContainingIgnoreCaseAndAvailableIsTrue(item1.getName(),
                item1.getName(), Pageable.unpaged()).toList();
        assertNotNull(items);
        assertEquals(1, items.size());
        assertEquals(items.get(0), item1);

    }

    @Test
    void findAllByOwner() {
        List<Item> items = itemRepository.findAllByOwner(user2, Pageable.unpaged()).toList();
        assertNotNull(items);
        assertEquals(1, items.size());
        assertEquals(items.get(0), item2);
    }

}