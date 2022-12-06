package ru.practicum.shareit.item.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.booking.dto.BookingItemDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exceptions.EntityNotFoundException;
import ru.practicum.shareit.item.comment.Comment;
import ru.practicum.shareit.item.comment.CommentDto;
import ru.practicum.shareit.item.comment.CommentMapper;
import ru.practicum.shareit.item.comment.CommentRepository;
import ru.practicum.shareit.item.dto.ItemBookingDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.repository.UserRepository;

import javax.validation.ValidationException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
@AllArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final ItemRequestRepository itemRequestRepository;

    @Override
    public ItemDto create(Item item, Long userId) {
        validate(item);
        item.setOwner(userRepository.findById(userId).get());
        if (item.getItemRequest() != null) {
            item.setItemRequest(itemRequestRepository.findById(item.getItemRequest().getId()).get());
        }
        Item createItem = itemRepository.save(item);
        log.info("Предмет с id = '{}' добавлен в список", createItem.getId());
        ItemDto createItemDto = ItemMapper.toItemDto(createItem);
        if (item.getItemRequest() != null) {
            createItemDto.setRequestId(item.getItemRequest().getId());
        }
        return createItemDto;
    }

    @Override
    public List<Item> readAll(Integer from, Integer size) {
        if (from < 0 || size <= 0) {
            log.info("Параметры поиска введены некоректно");
            throw new IllegalArgumentException("Параметры поиска введены некоректно");
        }
        Pageable pageable = PageRequest.of(from / size, size);
        return itemRepository.findAll(pageable).toList();
    }

    @Override
    public List<ItemBookingDto> readAllByUserId(Long id, Integer from, Integer size) {
        if (from < 0 || size <= 0) {
            log.info("Параметры поиска введены некоректно");
            throw new IllegalArgumentException("Параметры поиска введены некоректно");
        }
        Pageable pageable = PageRequest.of(from / size, size);

        Map<Item, List<Comment>> comments =
                commentRepository.findAll().stream().collect(Collectors.groupingBy(Comment::getItem));
        Map<Item, List<Booking>> bookings = bookingRepository.findAllByStatusOrderByStartDesc(Status.APPROVED).stream()
                .collect(Collectors.groupingBy(Booking::getItem));

        List<Item> items = itemRepository.findAllByOwner(userRepository.findById(id).get(), pageable).toList();

        List<ItemBookingDto> itemsBookingDto = new ArrayList<>();

        for (Item item : items) {
            ItemBookingDto itemBookingDto = ItemMapper.toItemWishBookingAndCommentDto(item, null,
                    null, List.of());
            if (bookings.containsKey(item)) {
                itemBookingDto = addLastAndNextBooking(item, bookings.get(item), List.of());
            }
            if (comments.containsKey(item)) {
                List<CommentDto> commentsDto = comments.get(item)
                        .stream()
                        .map(CommentMapper::toCommentDto)
                        .collect(Collectors.toList());
                itemBookingDto.setComments(commentsDto);
            }
            itemsBookingDto.add(itemBookingDto);
        }
        return itemsBookingDto;
    }

    @Override
    public Item update(Long id, Item item, Long userId) {
        if (itemRepository.findById(id).isPresent()) {
            if (itemRepository.findById(id).get().getOwner() != userRepository.findById(userId).get()) {
                throw new EntityNotFoundException("EntityNotFoundException (Предмет не может быть обновлен, т.к. он " +
                        "не принадлежит данному пользователю)");
            }
            if (item.getName() != null) {
                itemRepository.findById(id).get().setName(item.getName());
            }
            if (item.getDescription() != null) {
                itemRepository.findById(id).get().setDescription(item.getDescription());
            }
            if (item.getAvailable() != null) {
                itemRepository.findById(id).get().setAvailable(item.getAvailable());
            }
            Item updateItem = itemRepository.save(itemRepository.findById(id).get());
            log.info("Предмет с id = '{}' обновлен", updateItem.getId());
            return updateItem;
        } else {
            log.info("EntityNotFoundException (Предмет не может быть обновлен, т.к. его нет в списке)");
            throw new EntityNotFoundException("Предмет не может быть обновлен, т.к. его нет в списке");
        }
    }

    @Override
    public Item getItemById(Long id) {
        if (itemRepository.findById(id).isPresent()) {
            return itemRepository.findById(id).get();
        } else {
            throw new EntityNotFoundException(String.format("Предмет с id=%d отсутствует в списке", id));
        }
    }

    @Override
    public ItemBookingDto getItemByUserId(Long itemId, Long userId) {
        if (itemRepository.findById(itemId).isEmpty()) {
            throw new EntityNotFoundException(String.format("Предмет с id = %d отсутствует в списке", itemId));
        } else {
            Item item = itemRepository.findById(itemId).get();
            List<Comment> comments = commentRepository.findAllByItem(itemRepository.findById(itemId).get());
            if (item.getOwner().equals(userRepository.findById(userId).get())) {
                List<Booking> bookings =
                        bookingRepository.findAllByItem_IdOrderByStartDesc(itemId);
                if (bookings.size() != 0) {
                    return addLastAndNextBooking(item, bookings, comments);
                } else {
                    return addLastAndNextBooking(item, List.of(), comments);
                }
            } else {
                return addLastAndNextBooking(item, List.of(), comments);
            }
        }
    }

    @Override
    public void delete(Long id, Long userId) {
        validate(getItemById(id));
        if (itemRepository.findById(id).isPresent()) {
            itemRepository.deleteById(id);
        } else {
            throw new EntityNotFoundException(String.format("Предмет с id=%d отсутствует в списке", id));
        }
    }

    @Override
    public List<ItemDto> findItemsByText(String text, Integer from, Integer size) {
        if (from < 0 || size <= 0) {
            log.info("Параметры поиска введены некоректно");
            throw new IllegalArgumentException("Параметры поиска введены некоректно");
        }
        Pageable pageable = PageRequest.of(from / size, size);
        if (text.isEmpty() && text.isBlank()) {
            return new ArrayList<>();
        } else {
            List<Item> items = itemRepository.findByNameOrDescriptionContainingIgnoreCaseAndAvailableIsTrue(text, text,
                    pageable).toList();
            return items
                    .stream()
                    .map(ItemMapper::toItemDto)
                    .collect(Collectors.toList());
        }
    }

    @Override
    public CommentDto createComment(Long itemId, Long userId, Comment comment) {
        if (itemRepository.findById(itemId).isPresent()) {
            if (!bookingRepository.findAllByItemIdAndAndBooker_IdAndEndBefore(itemId, userId, LocalDateTime.now())
                    .isEmpty()) {
                comment.setItem(itemRepository.findById(itemId).get());
                comment.setAuthor(userRepository.findById(userId).get());
                comment.setCreated(LocalDateTime.now());
                return CommentMapper.toCommentDto(commentRepository.save(comment));
            } else {
                throw new IllegalArgumentException(String.format("Пользователь с id=%d не имел, либо не завершил бронь " +
                        "с предметом с id=%d", userId, itemId));
            }
        } else {
            throw new EntityNotFoundException(String.format("Предмет с id=%d отсутствует в списке", itemId));
        }
    }

    private void validate(Item item) {
        if (item.getName().isEmpty()) {
            log.info("ValidationException (Пустое название)");
            throw new ValidationException("Пустое название");
        }
        if (item.getDescription().isEmpty()) {
            log.info("ValidationException (Пустое описание)");
            throw new ValidationException("Пустое описание");
        }
        if (item.getAvailable() == null) {
            log.info("ValidationException (Ошибка статуса предмета с id = {})", item.getId());
            throw new IllegalArgumentException("Ошибка статуса предмет");
        }
    }

    private ItemBookingDto addLastAndNextBooking(Item item, List<Booking> bookings, List<Comment> comments) {
        ItemBookingDto itemBookingDto = ItemMapper.toItemWishBookingAndCommentDto(item, null, null,
                null);
        List<BookingItemDto> lastBookings = bookings.stream()
                .filter(booking -> booking.getEnd().isBefore(LocalDateTime.now()))
                .map(BookingMapper::toShortDto)
                .collect(Collectors.toList());
        if (lastBookings.size() != 0) {
            itemBookingDto.setLastBooking(lastBookings.get(0));
        }
        List<BookingItemDto> nextBookings = bookings.stream()
                .filter(booking -> booking.getStart().isAfter(LocalDateTime.now()))
                .map(BookingMapper::toShortDto)
                .collect(Collectors.toList());
        if (nextBookings.size() != 0) {
            itemBookingDto.setNextBooking(nextBookings.get(nextBookings.size() - 1));
        }
        List<CommentDto> commentsDto = comments
                .stream()
                .map(CommentMapper::toCommentDto)
                .collect(Collectors.toList());
        itemBookingDto.setComments(commentsDto);
        return itemBookingDto;
    }
}
