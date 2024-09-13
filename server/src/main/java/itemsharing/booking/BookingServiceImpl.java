package itemsharing.booking;

import itemsharing.booking.dao.BookingRepository;
import itemsharing.booking.dto.BookingDto;
import itemsharing.booking.dto.BookingRequestDto;
import itemsharing.booking.model.Booking;
import itemsharing.booking.model.BookingStatus;
import itemsharing.exception.NotFoundException;
import itemsharing.exception.ValidationException;
import itemsharing.item.dao.ItemRepository;
import itemsharing.item.model.Item;
import itemsharing.user.dao.UserRepository;
import itemsharing.user.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Objects;

@RequiredArgsConstructor
@Service
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    public BookingDto create(Long userId, BookingRequestDto bookingRequestDto) {
        validate(userId, bookingRequestDto);
        Item item = itemRepository.findById(bookingRequestDto.getItemId()).orElseThrow(() -> {
            throw new NotFoundException("Item id = " + bookingRequestDto.getItemId() + " not found!");
        });
        User booker = userRepository.findById(userId).orElseThrow(() -> {
            throw new NotFoundException("User id = " + userId + " not found!");
        });
        return BookingMapper.toBookingDto(bookingRepository.save(
                Booking.builder()
                        .start(bookingRequestDto.getStart())
                        .end(bookingRequestDto.getEnd())
                        .item(item)
                        .booker(booker)
                        .status(BookingStatus.WAITING)
                        .build()
        ));
    }

    @Override
    public BookingDto setApproved(Long userId, Long bookingId, Boolean approved) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> {
            throw new NotFoundException("Booking id = " + bookingId + " not found!");
        });
        if (!userRepository.existsById(userId)) {
            throw new ValidationException("User id = " + userId + " not found!");
        }
        if (!booking.getItem().getOwner().getId().equals(userId)) {
            throw new NotFoundException("Only owner can set approved!");
        }
        if (booking.getStatus().equals(BookingStatus.APPROVED)) {
            throw new ValidationException("Status is APPROVED!");
        }
        if (approved) {
            booking.setStatus(BookingStatus.APPROVED);
        } else {
            booking.setStatus(BookingStatus.REJECTED);
        }
        bookingRepository.save(booking);
        return BookingMapper.toBookingDto(booking);
    }

    @Override
    public BookingDto findById(Long bookingId, Long userId) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> {
            throw new NotFoundException("Booking id = " + bookingId + " not found!");
        });
        if (!booking.getBooker().getId().equals(userId) && !booking.getItem().getOwner().getId().equals(userId)) {
            throw new NotFoundException("Only owner or booker can get Booking!");
        }
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("User id = " + userId + " not found!");
        }
        return BookingMapper.toBookingDto(booking);
    }

    @Override
    public Collection<BookingDto> findAllByBookerAndStatus(Long userId, String state) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("User id = " + userId + " not found!");
        }
        switch (state) {
            case "ALL":
                return bookingRepository.findAllByBookerIdOrderByStartDesc(userId).stream()
                        .map(BookingMapper::toBookingDto)
                        .toList();
            case "CURRENT":
                return bookingRepository.findAllByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(userId, LocalDateTime.now(), LocalDateTime.now()).stream()
                        .map(BookingMapper::toBookingDto)
                        .toList();
            case "PAST":
                return bookingRepository.findAllByBookerIdAndEndBeforeOrderByStartDesc(userId, LocalDateTime.now()).stream()
                        .map(BookingMapper::toBookingDto)
                        .toList();
            case "FUTURE":
                return bookingRepository.findAllByBookerIdAndStartAfterOrderByStartDesc(userId, LocalDateTime.now()).stream()
                        .map(BookingMapper::toBookingDto)
                        .toList();
            case "WAITING", "REJECTED":
                return bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(userId, BookingStatus.valueOf(state)).stream()
                        .map(BookingMapper::toBookingDto)
                        .toList();
            default:
                throw new RuntimeException("Unknown state: " + state);
        }
    }

    @Override
    public Collection<BookingDto> findAllByOwnerAndStatus(Long userId, String state) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("User id = " + userId + " not found!");
        }
        switch (state) {
            case "ALL":
                return bookingRepository.findAllByItemOwnerIdOrderByStartDesc(userId).stream()
                        .map(BookingMapper::toBookingDto)
                        .toList();
            case "CURRENT":
                return bookingRepository.findAllByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(userId, LocalDateTime.now(), LocalDateTime.now()).stream()
                        .map(BookingMapper::toBookingDto)
                        .toList();
            case "PAST":
                return bookingRepository.findAllByItemOwnerIdAndEndBeforeOrderByStartDesc(userId, LocalDateTime.now()).stream()
                        .map(BookingMapper::toBookingDto)
                        .toList();
            case "FUTURE":
                return bookingRepository.findAllByItemOwnerIdAndStartAfterOrderByStartDesc(userId, LocalDateTime.now()).stream()
                        .map(BookingMapper::toBookingDto)
                        .toList();
            case "WAITING", "REJECTED":
                return bookingRepository.findAllByItemOwnerIdAndStatusOrderByStartDesc(userId, BookingStatus.valueOf(state)).stream()
                        .map(BookingMapper::toBookingDto)
                        .toList();
            default:
                throw new RuntimeException("Unknown state: " + state);
        }
    }

    private void validate(Long userId, BookingRequestDto bookingRequestDto) {
        if (bookingRequestDto.getEnd() == null) {
            throw new ValidationException("End date is null!");
        }
        if (bookingRequestDto.getStart() == null) {
            throw new ValidationException("Start date is null!");
        }
        if (bookingRequestDto.getEnd().isBefore(LocalDateTime.now())) {
            throw new ValidationException("End date before now!");
        }
        if (bookingRequestDto.getStart().isBefore(LocalDateTime.now())) {
            throw new ValidationException("Start date before now!");
        }
        if (bookingRequestDto.getEnd().isBefore(bookingRequestDto.getStart())) {
            throw new ValidationException("End date before Start date!");
        }
        if (bookingRequestDto.getEnd().equals(bookingRequestDto.getStart())) {
            throw new ValidationException("End date equals Start date!");
        }
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("User id = " + userId + " not found!");
        }
        Item item = itemRepository.findById(bookingRequestDto.getItemId()).orElseThrow(
                () -> new NotFoundException("Item id = " + bookingRequestDto.getItemId() + " not found!"));
        if (Objects.equals(item.getOwner().getId(), userId)) {
            throw new NotFoundException("Item is already booked!");
        }
        if (Boolean.FALSE.equals(item.getAvailable())) {
            throw new ValidationException("Available is not true!");
        }
        if (bookingRepository.findAllByItemId(item.getId()).stream()
                .anyMatch(booking -> (booking.getStart().isAfter(bookingRequestDto.getStart())
                        && booking.getStart().isBefore(bookingRequestDto.getEnd()))
                        || (booking.getEnd().isAfter(bookingRequestDto.getStart())
                        && booking.getEnd().isBefore(bookingRequestDto.getEnd())))) {
            throw new ValidationException("Crossing dates!");
        }
    }
}
