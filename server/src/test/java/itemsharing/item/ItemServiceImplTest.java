package itemsharing.item;

import itemsharing.booking.BookingMapper;
import itemsharing.booking.dao.BookingRepository;
import itemsharing.booking.model.Booking;
import itemsharing.booking.model.BookingStatus;
import itemsharing.comment.CommentMapper;
import itemsharing.comment.dao.CommentRepository;
import itemsharing.comment.dto.CommentDto;
import itemsharing.comment.dto.CommentRequestDto;
import itemsharing.comment.model.Comment;
import itemsharing.item.dao.ItemRepository;
import itemsharing.item.dto.ItemDto;
import itemsharing.item.dto.ItemInfoDto;
import itemsharing.item.model.Item;
import itemsharing.request.dao.ItemRequestRepository;
import itemsharing.request.model.ItemRequest;
import itemsharing.user.dao.UserRepository;
import itemsharing.user.model.User;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.BEFORE_TEST_CLASS;

@Transactional
@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Sql(scripts = {"/item/test-add-items.sql"}, executionPhase = BEFORE_TEST_CLASS)
class ItemServiceImplTest {

    private ItemService itemService;
    private ItemRepository itemRepository;
    private CommentRepository commentRepository;
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRequestRepository itemRequestRepository;
    private Booking booking;
    private User user;
    private User owner;
    private Item item;
    private ItemDto itemDto;
    private ItemInfoDto itemInfoDto;
    private Comment comment;
    private CommentDto commentDto;
    private CommentRequestDto commentRequestDto;
    private ItemRequest itemRequest;

    @BeforeEach
    void setUp() {
        itemRepository = mock(ItemRepository.class);
        commentRepository = mock(CommentRepository.class);
        itemService = new ItemServiceImpl(
                itemRepository,
                userRepository,
                bookingRepository,
                commentRepository,
                itemRequestRepository);
        user = User.builder()
                .id(1L)
                .name("TestUserName")
                .email("UserEmail@test.com")
                .build();
        userRepository.save(user);
        owner = User.builder()
                .id(2L)
                .name("TestOwnerName")
                .email("OwnerEmail@test.com")
                .build();
        userRepository.save(owner);
        item = Item.builder()
                .id(1L)
                .name("TestItemName")
                .description("TestItemDescription")
                .request(itemRequest)
                .available(true)
                .owner(owner)
                .build();
        itemRequest = ItemRequest.builder()
                .id(1L)
                .requestor(user)
                .created(LocalDateTime.now())
                .description("TestItemRequestDescription")
                .build();
        itemRequestRepository.save(itemRequest);
        booking = Booking.builder()
                .id(1L)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .status(BookingStatus.WAITING)
                .booker(user)
                .item(item)
                .build();
        bookingRepository.save(booking);
        comment = Comment.builder()
                .id(1L)
                .text("TestCommentText")
                .item(item)
                .author(owner)
                .created(LocalDateTime.now())
                .build();
        commentDto = CommentMapper.toCommentDto(comment);
        commentRequestDto = new CommentRequestDto(comment.getText());
        itemDto = ItemMapper.toItemDto(item);
        itemInfoDto = ItemMapper.toItemInfoDto(item,
                BookingMapper.toBookingDateInfoDto(booking),
                BookingMapper.toBookingDateInfoDto(booking),
                List.of(CommentMapper.toCommentDto(comment))
        );

        when(itemRepository.save(any())).thenReturn(item);
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(itemRepository.findAll()).thenReturn(List.of(item));
        when(itemRepository.findAllByOwnerIdOrderByIdAsc(anyLong())).thenReturn(List.of(item));
        when(itemRepository.findByNameOrDescriptionContaining(anyString())).thenReturn(List.of(item));
        when(commentRepository.save(any())).thenReturn(comment);
    }

    @Test
    void findAll() {
        List<ItemDto> result = itemService.findAll().stream().toList();
        Assertions.assertNotNull(result);
        Assertions.assertEquals(result.getFirst().getName(), itemDto.getName());
        Assertions.assertEquals(result.getFirst().getDescription(), itemDto.getDescription());
        Assertions.assertEquals(result.getFirst().getAvailable(), itemDto.getAvailable());
        Assertions.assertEquals(result.getFirst().getRequestId(), itemDto.getRequestId());
        verify(itemRepository, times(1)).findAll();
    }

    @Test
    void create() {
        ItemDto result = itemService.create(user.getId(), itemDto);
        Assertions.assertNotNull(result);
        Assertions.assertEquals(result.getName(), itemDto.getName());
        Assertions.assertEquals(result.getDescription(), itemDto.getDescription());
        Assertions.assertEquals(result.getRequestId(), itemDto.getRequestId());
        Assertions.assertEquals(result.getAvailable(), itemDto.getAvailable());
        Assertions.assertEquals(result.getId(), itemDto.getId());
        verify(itemRepository, times(1)).save(any());
    }

    @Test
    void update() {
        ItemDto result = itemService.update(owner.getId(), item.getId(), itemDto);
        Assertions.assertNotNull(result);
        Assertions.assertEquals(result.getName(), itemDto.getName());
        Assertions.assertEquals(result.getDescription(), itemDto.getDescription());
        Assertions.assertEquals(result.getRequestId(), itemDto.getRequestId());
        Assertions.assertEquals(result.getAvailable(), itemDto.getAvailable());
        Assertions.assertEquals(result.getId(), itemDto.getId());
        verify(itemRepository, times(1)).save(any());
    }

    @Test
    void addComment() {
        booking = Booking.builder()
                .id(1L)
                .start(LocalDateTime.now().minusDays(1))
                .end(LocalDateTime.now().minusDays(2))
                .status(BookingStatus.APPROVED)
                .booker(user)
                .item(item)
                .build();
        bookingRepository.save(booking);
        CommentDto result = itemService.addComment(booking.getBooker().getId(), booking.getItem().getId(), commentRequestDto);
        Assertions.assertNotNull(result);
        Assertions.assertEquals(result.getId(), commentDto.getId());
        Assertions.assertEquals(result.getText(), commentDto.getText());
        Assertions.assertEquals(result.getAuthorName(), commentDto.getAuthorName());
        Assertions.assertEquals(result.getCreated(), commentDto.getCreated());
        verify(commentRepository, times(1)).save(any());
    }

    @Test
    void findItemById() {
        ItemInfoDto result = itemService.findItemById(user.getId(), item.getId());
        Assertions.assertNotNull(result);
        Assertions.assertEquals(result.getName(), itemInfoDto.getName());
        Assertions.assertEquals(result.getDescription(), itemInfoDto.getDescription());
        Assertions.assertEquals(result.getRequestId(), itemInfoDto.getRequestId());
        verify(itemRepository, times(1)).findById(anyLong());
    }

    @Test
    void findItemsByUserId() {
        List<ItemInfoDto> result = itemService.findItemsByUserId(user.getId()).stream().toList();
        Assertions.assertNotNull(result);
        Assertions.assertEquals(result.getFirst().getName(), itemInfoDto.getName());
        Assertions.assertEquals(result.getFirst().getDescription(), itemInfoDto.getDescription());
        Assertions.assertEquals(result.getFirst().getRequestId(), itemInfoDto.getRequestId());
        verify(itemRepository, times(1)).findAllByOwnerIdOrderByIdAsc(anyLong());
    }

    @Test
    void delete() {
        itemService.delete(item.getId());
        verify(itemRepository, times(1)).deleteById(anyLong());
    }

    @Test
    void findItemsByText() {
        List<ItemDto> result = itemService.findItemsByText("text").stream().toList();
        Assertions.assertNotNull(result);
        Assertions.assertEquals(result.getFirst().getName(), itemDto.getName());
        Assertions.assertEquals(result.getFirst().getDescription(), itemDto.getDescription());
        Assertions.assertEquals(result.getFirst().getAvailable(), itemDto.getAvailable());
        Assertions.assertEquals(result.getFirst().getRequestId(), itemDto.getRequestId());
        verify(itemRepository, times(1)).findByNameOrDescriptionContaining(anyString());
    }
}