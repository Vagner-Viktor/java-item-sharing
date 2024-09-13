package itemsharing.booking;

import itemsharing.booking.dao.BookingRepository;
import itemsharing.booking.dto.BookingDto;
import itemsharing.booking.dto.BookingRequestDto;
import itemsharing.booking.model.Booking;
import itemsharing.booking.model.BookingStatus;
import itemsharing.item.dao.ItemRepository;
import itemsharing.item.model.Item;
import itemsharing.user.dao.UserRepository;
import itemsharing.user.model.User;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class BookingServiceImplTest {

    private BookingService bookingService;
    private BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private Booking booking;
    private BookingDto bookingDto;
    private BookingRequestDto bookingRequestDto;
    private User user;
    private User owner;
    private Item item;

    @BeforeEach
    void setUp() {
        bookingRepository = mock(BookingRepository.class);
        bookingService = new BookingServiceImpl(bookingRepository, userRepository, itemRepository);
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
                .request(null)
                .available(true)
                .owner(owner)
                .build();
        itemRepository.save(item);
        booking = Booking.builder()
                .id(1L)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .status(BookingStatus.WAITING)
                .booker(user)
                .item(item)
                .build();
        bookingRequestDto = new BookingRequestDto();
        bookingRequestDto.setItemId(item.getId());
        bookingRequestDto.setStart(booking.getStart());
        bookingRequestDto.setEnd(booking.getEnd());
        bookingDto = BookingMapper.toBookingDto(booking);

        when(bookingRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));
        when(bookingRepository.findAllByBookerIdOrderByStartDesc(anyLong())).thenReturn(List.of(booking));
        when(bookingRepository.findAllByItemOwnerIdOrderByStartDesc(anyLong())).thenReturn(List.of(booking));
    }

    @Test
    void create() {
        final BookingDto result = bookingService.create(user.getId(), bookingRequestDto);
        Assertions.assertNotNull(result);
        Assertions.assertEquals(result.getStart(), bookingDto.getStart());
        Assertions.assertEquals(result.getEnd(), bookingDto.getEnd());
        Assertions.assertEquals(result.getItem(), bookingDto.getItem());
        Assertions.assertEquals(result.getStatus(), bookingDto.getStatus());
        Assertions.assertEquals(result.getBooker(), bookingDto.getBooker());
        verify(bookingRepository, times(1)).save(any());
    }

    @Test
    void setApproved() {
        BookingDto result = bookingService.setApproved(owner.getId(), booking.getId(), true);
        Assertions.assertNotNull(result);
        Assertions.assertEquals(result.getStart(), bookingDto.getStart());
        Assertions.assertEquals(result.getEnd(), bookingDto.getEnd());
        Assertions.assertEquals(result.getItem(), bookingDto.getItem());
        Assertions.assertEquals(result.getStatus(), BookingStatus.APPROVED.toString());
        Assertions.assertEquals(result.getBooker(), bookingDto.getBooker());
        verify(bookingRepository, times(1)).save(any());
    }

    @Test
    void findById() {
        BookingDto result = bookingService.findById(booking.getId(), user.getId());
        Assertions.assertNotNull(result);
        Assertions.assertEquals(result.getStart(), bookingDto.getStart());
        Assertions.assertEquals(result.getEnd(), bookingDto.getEnd());
        Assertions.assertEquals(result.getItem(), bookingDto.getItem());
        Assertions.assertEquals(result.getStatus(), bookingDto.getStatus());
        Assertions.assertEquals(result.getBooker(), bookingDto.getBooker());
        verify(bookingRepository, times(1)).findById(anyLong());
    }

    @Test
    void findAllByBookerAndStatus() {
        List<BookingDto> result = bookingService.findAllByBookerAndStatus(user.getId(), "ALL").stream().toList();
        Assertions.assertNotNull(result);
        Assertions.assertEquals(result.getFirst().getStart(), bookingDto.getStart());
        Assertions.assertEquals(result.getFirst().getEnd(), bookingDto.getEnd());
        Assertions.assertEquals(result.getFirst().getItem(), bookingDto.getItem());
        Assertions.assertEquals(result.getFirst().getStatus(), bookingDto.getStatus());
        Assertions.assertEquals(result.getFirst().getBooker(), bookingDto.getBooker());
        verify(bookingRepository, times(1)).findAllByBookerIdOrderByStartDesc(anyLong());
    }

    @Test
    void findAllByOwnerAndStatus() {
        List<BookingDto> result = bookingService.findAllByOwnerAndStatus(owner.getId(), "ALL").stream().toList();
        Assertions.assertNotNull(result);
        Assertions.assertEquals(result.getFirst().getStart(), bookingDto.getStart());
        Assertions.assertEquals(result.getFirst().getEnd(), bookingDto.getEnd());
        Assertions.assertEquals(result.getFirst().getItem(), bookingDto.getItem());
        Assertions.assertEquals(result.getFirst().getStatus(), bookingDto.getStatus());
        Assertions.assertEquals(result.getFirst().getBooker(), bookingDto.getBooker());
        verify(bookingRepository, times(1)).findAllByItemOwnerIdOrderByStartDesc(anyLong());
    }
}