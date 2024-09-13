package itemsharing.item;

import itemsharing.booking.dto.BookingDateInfoDto;
import itemsharing.comment.dto.CommentDto;
import itemsharing.item.dto.ItemDto;
import itemsharing.item.dto.ItemInfoDto;
import itemsharing.item.model.Item;

import java.util.Collection;
import java.util.List;

public class ItemMapper {
    public static Collection<ItemDto> toItemsDtoCollection(Collection<Item> items) {
        if (items == null) return List.of();
        return items.stream()
                .map(ItemMapper::toItemDto)
                .toList();
    }

    public static ItemDto toItemDto(Item item) {
        if (item == null) return null;
        return new ItemDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                item.getRequest() != null ? item.getRequest().getId() : null
        );
    }

    public static ItemInfoDto toItemInfoDto(Item item,
                                            BookingDateInfoDto lastBooking,
                                            BookingDateInfoDto nextBooking,
                                            Collection<CommentDto> commentDtos) {
        if (item == null) return null;
        return ItemInfoDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .requestId(item.getRequest() != null ? item.getRequest().getId() : null)
                .lastBooking(lastBooking)
                .nextBooking(nextBooking)
                .comments(commentDtos)
                .build();
    }
}
