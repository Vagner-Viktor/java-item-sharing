package itemsharing.request;

import itemsharing.exception.NotFoundException;
import itemsharing.request.dao.ItemRequestRepository;
import itemsharing.request.dto.ItemRequestDto;
import itemsharing.request.dto.ItemRequestRequestDto;
import itemsharing.request.model.ItemRequest;
import itemsharing.user.dao.UserRepository;
import itemsharing.user.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@Service
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository itemRequestRepository;
    private final UserRepository userRepository;

    @Override
    public List<ItemRequestDto> findAllByUserId(Long userId) {
        return ItemRequestMapper.toItemRequestDtoList(itemRequestRepository.findAllByRequestorId(userId));
    }

    @Override
    public ItemRequestDto create(ItemRequestRequestDto itemRequestRequestDto) {
        User requestor = userRepository.findById(itemRequestRequestDto.getRequestorId()).orElseThrow(() -> {
            throw new NotFoundException("User id = " + itemRequestRequestDto.getRequestorId() + " not found!");
        });
        ItemRequest itemRequest = itemRequestRepository.save(
                ItemRequest.builder()
                        .description(itemRequestRequestDto.getDescription())
                        .requestor(requestor)
                        .created(LocalDateTime.now())
                        .build()
        );
        return ItemRequestMapper.toItemRequestDto(itemRequest);
    }

    @Override
    public ItemRequestDto findItemRequestById(Long itemRequestId) {
        return ItemRequestMapper.toItemRequestDto(
                itemRequestRepository.findByIdOrderByCreatedAsc(itemRequestId).orElseThrow(() -> {
                    throw new NotFoundException("ItemRequest id = " + itemRequestId + " not found!");
                }));
    }

    @Override
    public List<ItemRequestDto> findAllUsersItemRequest(Pageable pageable) {
        return itemRequestRepository.findAll(pageable).get()
                .map(ItemRequestMapper::toItemRequestDto)
                .toList();
    }
}
