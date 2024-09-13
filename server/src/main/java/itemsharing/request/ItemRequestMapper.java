package itemsharing.request;

import itemsharing.item.ItemMapper;
import itemsharing.request.dto.ItemRequestDto;
import itemsharing.request.model.ItemRequest;
import itemsharing.user.UserMapper;

import java.util.List;

public class ItemRequestMapper {

    public static List<ItemRequestDto> toItemRequestDtoList(List<ItemRequest> itemRequests) {
        return itemRequests.stream()
                .map(ItemRequestMapper::toItemRequestDto)
                .toList();
    }

    public static ItemRequestDto toItemRequestDto(ItemRequest itemRequest) {
        if (itemRequest == null) return null;
        return ItemRequestDto.builder()
                .id(itemRequest.getId())
                .description(itemRequest.getDescription())
                .requestor(UserMapper.toUserDto(itemRequest.getRequestor()))
                .created(itemRequest.getCreated())
                .items(ItemMapper.toItemsDtoCollection(itemRequest.getItems() != null ? itemRequest.getItems() : List.of()))
                .build();
    }
}
