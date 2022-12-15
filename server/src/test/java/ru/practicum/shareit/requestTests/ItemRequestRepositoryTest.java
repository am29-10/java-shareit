package ru.practicum.shareit.requestTests;

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

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class ItemRequestRepositoryTest {

    @Autowired
    ItemRequestRepository requestRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    ItemRepository itemRepository;


    User user;
    Item item;
    ItemRequest request;

    @BeforeEach
    void beforeEach() {
        user = userRepository.save(User.builder()
                .id(1L)
                .name("user1")
                .email("user1@mail.ru")
                .build());
        request = requestRepository.save(ItemRequest.builder()
                .id(1L)
                .description("description")
                .requestor(user)
                .created(LocalDateTime.now())
                .build());
        item = itemRepository.save(Item.builder()
                .id(1L)
                .name("item1")
                .description("description1")
                .available(true)
                .owner(user)
                .itemRequest(request)
                .build());
    }

    @Test
    void findAllByRequestor_IdOrderByCreatedDesc() {
        List<ItemRequest> requests = requestRepository.findAllByRequestor_IdOrderByCreatedDesc(user.getId(),
                Pageable.unpaged()).toList();

        assertEquals(requests.size(), 1);
    }
}