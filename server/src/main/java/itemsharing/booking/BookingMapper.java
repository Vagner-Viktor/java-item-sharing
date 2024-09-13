package itemsharing.booking;

import itemsharing.booking.dto.BookingDateInfoDto;
import itemsharing.booking.dto.BookingDto;
import itemsharing.booking.model.Booking;
import itemsharing.item.ItemMapper;
import itemsharing.user.UserMapper;

public class BookingMapper {
    public static BookingDto toBookingDto(Booking booking) {
        if (booking == null) return null;
        return BookingDto.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .item(ItemMapper.toItemDto(booking.getItem()))
                .booker(UserMapper.toUserDto(booking.getBooker()))
                .status(booking.getStatus().name())
                .build();
    }

    public static BookingDateInfoDto toBookingDateInfoDto(Booking booking) {
        if (booking == null) return null;
        return BookingDateInfoDto.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .bookerId(booking.getBooker().getId())
                .build();
    }
}
