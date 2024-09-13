package itemsharing.item;

import itemsharing.booking.BookingMapper;
import itemsharing.booking.dao.BookingRepository;
import itemsharing.booking.model.BookingStatus;
import itemsharing.comment.CommentMapper;
import itemsharing.comment.dao.CommentRepository;
import itemsharing.comment.dto.CommentDto;
import itemsharing.comment.dto.CommentRequestDto;
import itemsharing.comment.model.Comment;
import itemsharing.exception.NotFoundException;
import itemsharing.exception.ValidationException;
import itemsharing.item.dao.ItemRepository;
import itemsharing.item.dto.ItemDto;
import itemsharing.item.dto.ItemInfoDto;
import itemsharing.item.model.Item;
import itemsharing.request.dao.ItemRequestRepository;
import itemsharing.request.model.ItemRequest;
import itemsharing.user.dao.UserRepository;
import itemsharing.user.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

@RequiredArgsConstructor
@Service
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final ItemRequestRepository itemRequestRepository;

    @Override
    public Collection<ItemDto> findAll() {
        return ItemMapper.toItemsDtoCollection(itemRepository.findAll());
    }

    @Override
    public ItemDto create(Long userId, ItemDto itemDto) {
        validation(userId, null);
        User user = userRepository.findById(userId).orElseThrow(() -> {
            throw new NotFoundException("User id = " + userId + " not found!");
        });
        ItemRequest itemRequest = null;
        if (itemDto.getRequestId() != null) {
            itemRequest = itemRequestRepository.findById(itemDto.getRequestId()).orElseThrow(() -> {
                throw new NotFoundException("ItemRequest id = " + itemDto.getRequestId() + " not found!");
            });
        }
        return ItemMapper.toItemDto(itemRepository.save(
                Item.builder()
                        .name(itemDto.getName())
                        .owner(user)
                        .description(itemDto.getDescription())
                        .available(itemDto.getAvailable())
                        .request(itemRequest)
                        .build()
        ));
    }

    @Override
    public ItemDto update(Long userId, Long itemId, ItemDto itemDto) {
        validation(userId, itemId);

        Item item = itemRepository.findById(itemId).orElseThrow(() -> {
            throw new NotFoundException("Item id = " + itemId + " not found!");
        });
        if (itemDto.getName() != null
                && !itemDto.getName().isBlank()) {
            item.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null
                && !itemDto.getDescription().isBlank()) {
            item.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            item.setAvailable(itemDto.getAvailable());
        }
        return ItemMapper.toItemDto(itemRepository.save(item));
    }

    @Override
    public CommentDto addComment(Long userId, Long itemId, CommentRequestDto commentRequestDto) {
        if (commentRequestDto == null || commentRequestDto.getText().isEmpty() || commentRequestDto.getText().isBlank()) {
            throw new ValidationException("Comment is empty!");
        }
        if (bookingRepository.findAllByBookerIdAndItemIdAndStatusAndEndBefore(userId, itemId, BookingStatus.APPROVED, LocalDateTime.now()).isEmpty()) {
            throw new ValidationException("The user (id = " + userId + ") did not book this item (id = " + itemId + ") for rent");
        }
        return CommentMapper.toCommentDto(commentRepository.save(Comment.builder()
                .author(userRepository.findById(userId).orElseThrow(() -> {
                    throw new NotFoundException("User id = " + userId + " not found!");
                }))
                .item(itemRepository.findById(itemId).orElseThrow(() -> {
                    throw new NotFoundException("Item id = " + itemId + " not found!");
                }))
                .text(commentRequestDto.getText())
                .created(LocalDateTime.now())
                .build()));
    }

    @Override
    public ItemInfoDto findItemById(Long userId, Long itemId) {
        Item item = itemRepository.findById(itemId).orElseThrow(() -> {
            throw new NotFoundException("Item (id = " + itemId + ") not found!");
        });
        return ItemMapper.toItemInfoDto(
                item,
                BookingMapper.toBookingDateInfoDto(bookingRepository.findFirstByItemIdAndItemOwnerIdAndStartBeforeAndStatusOrderByStartDesc(itemId, userId, LocalDateTime.now(), BookingStatus.APPROVED).orElse(null)),
                BookingMapper.toBookingDateInfoDto(bookingRepository.findFirstByItemIdAndItemOwnerIdAndStartAfterAndStatusOrderByStartAsc(itemId, userId, LocalDateTime.now(), BookingStatus.APPROVED).orElse(null)),
                CommentMapper.toCommentsDtoCollection(commentRepository.findAllByItemId(itemId))
        );
    }

    @Override
    public Collection<ItemInfoDto> findItemsByUserId(Long userId) {
        return itemRepository.findAllByOwnerIdOrderByIdAsc(userId).stream()
                .map(item -> ItemMapper.toItemInfoDto(item,
                        BookingMapper.toBookingDateInfoDto(item.getBookings() == null || item.getBookings().isEmpty() ? null : item.getBookings().getFirst()),
                        BookingMapper.toBookingDateInfoDto(item.getBookings() == null || item.getBookings().isEmpty() ? null : item.getBookings().getLast()),
                        CommentMapper.toCommentsDtoCollection(item.getComments())))
                .toList();
    }

    @Override
    public void delete(Long itemId) {
        itemRepository.deleteById(itemId);
    }

    @Override
    public Collection<ItemDto> findItemsByText(String text) {
        if (text.isBlank() || text.isEmpty()) return List.of();
        return ItemMapper.toItemsDtoCollection(itemRepository.findByNameOrDescriptionContaining(text));
    }

    private void validation(Long userId, Long itemId) {
        if (userId == null) {
            throw new ValidationException("Owner id not specified!");
        }
        if (itemId != null && !(Objects.equals(Objects.requireNonNull(itemRepository.findById(itemId).orElse(null)).getOwner().getId(), userId))) {
            throw new NotFoundException("Only the owner can edit an item!");
        }
    }
}
