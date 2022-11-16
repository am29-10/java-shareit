package ru.practicum.shareit.item.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
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
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.repository.UserRepository;

import javax.validation.ValidationException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@AllArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;

    private final CommentRepository commentRepository;

    @Override
    public Item create(Item item, Long userId) {
        validate(item, userId);
        item.setOwner(userRepository.findById(userId).get());
        Item createItem = itemRepository.save(item);
        log.info("Предмет с id = '{}' добавлен в список", createItem.getId());
        return createItem;
    }

    @Override
    public List<Item> readAll() {
        return itemRepository.findAll();
    }

    @Override
    public List<ItemBookingDto> readAllByUserId(Long id) {
        List<ItemBookingDto> itemsWithBooking = new ArrayList<>();
        List<CommentDto> commentsDto = new ArrayList<>();
        if (userRepository.findById(id).isPresent()) {
            List<Item> items = itemRepository.findAllByOwnerId(id);
            if (items == null) {
                throw new EntityNotFoundException(String.format("EntityNotFoundException (У пользователя с id = %d " +
                        "отсутствуют личные предметы)", id));
            }
            itemsWithBooking = new ArrayList<>();
            for (Item item : items) {
                BookingItemDto lastBooking = null;
                BookingItemDto nextBooking = null;
                Booking last = bookingRepository.findFirstByItem_Owner_IdAndAndItem_IdOrderByStart(id, item.getId());
                Booking next = bookingRepository.findFirstByItem_OwnerIdAndIdOrderByStartDesc(id, item.getId());
                if (last != null) {
                    lastBooking = BookingMapper.toShortDto(last);
                }
                if (next != null) {
                    nextBooking = BookingMapper.toShortDto(next);
                }
                itemsWithBooking.add(ItemMapper.toItemWishBookingAndCommentDto(item, lastBooking, nextBooking,
                        commentsDto));
            }
        }
        List<Comment> comments = commentRepository.findAllByItemId(id);
        for (Comment comment : comments) {
            CommentDto commentDto = CommentMapper.toCommentDto(comment);
            commentsDto.add(commentDto);
        }
        return itemsWithBooking;
    }

    @Override
    public Item update(Long id, Item item, Long userId) {
        if (itemRepository.findById(id).get().getOwner() != userRepository.findById(userId).get()) {
            throw new EntityNotFoundException("EntityNotFoundException (Предмет не может быть обновлен, т.к. он " +
                    "не принадлежит данному пользователь)");
        }
        if (itemRepository.findById(id).isPresent()) {
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
    public ItemBookingDto getItemByUserId(Long id, Long userId) {
        Item item;
        BookingItemDto lastBooking = null;
        BookingItemDto nextBooking = null;
        List<CommentDto> commentsDto = new ArrayList<>();
        if (userRepository.findById(userId).isPresent()) {
            if (itemRepository.findById(id).isPresent()) {
                item = getItemById(id);
                Booking last = bookingRepository.findFirstByItem_Owner_IdAndAndItem_IdOrderByStart(userId, id);
                if (last != null) {
                    lastBooking = BookingMapper.toShortDto(last);
                }
                Booking next = bookingRepository.findFirstByItem_OwnerIdAndIdOrderByStartDesc(userId, id);
                if (next != null) {
                    nextBooking = BookingMapper.toShortDto(next);
                }
                List<Comment> comments = commentRepository.findAllByItemId(id);
                for (Comment comment : comments) {
                    CommentDto commentDto = CommentMapper.toCommentDto(comment);
                    commentsDto.add(commentDto);
                }
            } else {
                throw new EntityNotFoundException(String.format("EntityNotFoundException (Предмет с id = %d " +
                        "отсутствует в списке)", id));
            }
        } else {
            throw new EntityNotFoundException(String.format("EntityNotFoundException (Пользователь с id = %d " +
                    "отсутствует в списке)", userId));
        }
        return ItemMapper.toItemWishBookingAndCommentDto(item, lastBooking, nextBooking, commentsDto);
    }

    @Override
    public void delete(Long id, Long userId) {
        validate(getItemById(id), userId);
        if (itemRepository.findById(id).isPresent()) {
            itemRepository.deleteById(id);
        } else {
            throw new EntityNotFoundException(String.format("Предмет с id=%d отсутствует в списке", id));
        }
    }

    @Override
    public List<Item> findItemsByText(String text) {
        if (text.isEmpty() && text.isBlank()) {
            return new ArrayList<>();
        } else {
            return itemRepository.findByNameOrDescriptionContainingIgnoreCaseAndAvailableIsTrue(text, text);
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
                CommentDto commentDto = CommentMapper.toCommentDto(commentRepository.save(comment));
                return commentDto;
            } else {
                throw new IllegalArgumentException(String.format("Пользователь с id=%d не имел, либо не завершил бронь " +
                        "с предметом с id=%d", userId, itemId));
            }
        } else {
            throw new EntityNotFoundException(String.format("Предмет с id=%d отсутствует в списке", itemId));
        }
    }


    private void validate(Item item, Long userId) {
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
}
