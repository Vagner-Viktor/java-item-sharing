package itemsharing.request;

import itemsharing.request.dto.ItemRequestDto;
import itemsharing.request.dto.ItemRequestRequestDto;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ItemRequestService {
    List<ItemRequestDto> findAllByUserId(Long userId);

    ItemRequestDto create(ItemRequestRequestDto itemRequestRequestDto);

    ItemRequestDto findItemRequestById(Long itemRequestId);

    List<ItemRequestDto> findAllUsersItemRequest(Pageable pageable);
}
