package itemsharing.item.dto;

import itemsharing.booking.dto.BookingDateInfoDto;
import itemsharing.comment.dto.CommentDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collection;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ItemInfoDto {
    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private Long requestId;
    private Collection<CommentDto> comments;
    private BookingDateInfoDto lastBooking;
    private BookingDateInfoDto nextBooking;
}
