package itemsharing.request;

import itemsharing.request.dao.ItemRequestRepository;
import itemsharing.request.dto.ItemRequestDto;
import itemsharing.request.dto.ItemRequestRequestDto;
import itemsharing.request.model.ItemRequest;
import itemsharing.user.dao.UserRepository;
import itemsharing.user.model.User;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

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
class ItemRequestServiceImplTest {

    private ItemRequestService itemRequestService;
    private ItemRequestRepository itemRequestRepository;
    private final UserRepository userRepository;
    private User user;
    private ItemRequest itemRequest;
    private ItemRequestDto itemRequestDto;
    private ItemRequestRequestDto itemRequestRequestDto;
    private Pageable pageable;

    @BeforeEach
    void setUp() {
        itemRequestRepository = mock(ItemRequestRepository.class);
        itemRequestService = new ItemRequestServiceImpl(
                itemRequestRepository,
                userRepository);
        user = User.builder()
                .id(1L)
                .name("TestUserName")
                .email("UserEmail@test.com")
                .build();
        userRepository.save(user);
        itemRequest = ItemRequest.builder()
                .id(1L)
                .requestor(user)
                .created(LocalDateTime.now())
                .description("TestItemRequestDescription")
                .build();
        itemRequestDto = ItemRequestMapper.toItemRequestDto(itemRequest);
        itemRequestRequestDto = new ItemRequestRequestDto(user.getId(), "TestItemRequestText");

        pageable = PageRequest.of(0, 10, Sort.by("created").descending());
        final int start = (int) pageable.getOffset();
        final int end = Math.min((start + pageable.getPageSize()), List.of(itemRequest).size());
        final Page<ItemRequest> page = new PageImpl<>(List.of(itemRequest).subList(start, end), pageable, List.of(itemRequest).size());
        when(itemRequestRepository.findAll((Pageable) any())).thenReturn(page);

        when(itemRequestRepository.save(any())).thenReturn(itemRequest);
        when(itemRequestRepository.findByIdOrderByCreatedAsc(anyLong())).thenReturn(Optional.of(itemRequest));
        when(itemRequestRepository.findAllByRequestorId(any())).thenReturn(List.of(itemRequest));
    }

    @Test
    void findAllByUserId() {
        List<ItemRequestDto> result = itemRequestService.findAllByUserId(user.getId());
        Assertions.assertNotNull(result);
        Assertions.assertEquals(result.getFirst().getCreated(), itemRequestDto.getCreated());
        Assertions.assertEquals(result.getFirst().getDescription(), itemRequestDto.getDescription());
        Assertions.assertEquals(result.getFirst().getRequestor(), itemRequestDto.getRequestor());
        Assertions.assertEquals(result.getFirst().getId(), itemRequestDto.getId());
        verify(itemRequestRepository, times(1)).findAllByRequestorId(anyLong());
    }

    @Test
    void create() {
        ItemRequestDto result = itemRequestService.create(itemRequestRequestDto);
        Assertions.assertNotNull(result);
        Assertions.assertEquals(result.getCreated(), itemRequestDto.getCreated());
        Assertions.assertEquals(result.getDescription(), itemRequestDto.getDescription());
        Assertions.assertEquals(result.getRequestor(), itemRequestDto.getRequestor());
        Assertions.assertEquals(result.getId(), itemRequestDto.getId());
        verify(itemRequestRepository, times(1)).save(any());
    }

    @Test
    void findItemRequestById() {
        ItemRequestDto result = itemRequestService.findItemRequestById(itemRequest.getRequestor().getId());
        Assertions.assertNotNull(result);
        Assertions.assertEquals(result.getCreated(), itemRequestDto.getCreated());
        Assertions.assertEquals(result.getDescription(), itemRequestDto.getDescription());
        Assertions.assertEquals(result.getRequestor(), itemRequestDto.getRequestor());
        Assertions.assertEquals(result.getId(), itemRequestDto.getId());
        verify(itemRequestRepository, times(1)).findByIdOrderByCreatedAsc(anyLong());

    }

    @Test
    void findAllUsersItemRequest() {
        List<ItemRequestDto> result = itemRequestService.findAllUsersItemRequest(pageable);
        Assertions.assertNotNull(result);
        Assertions.assertEquals(result.getFirst().getCreated(), itemRequestDto.getCreated());
        Assertions.assertEquals(result.getFirst().getDescription(), itemRequestDto.getDescription());
        Assertions.assertEquals(result.getFirst().getRequestor(), itemRequestDto.getRequestor());
        Assertions.assertEquals(result.getFirst().getId(), itemRequestDto.getId());
        verify(itemRequestRepository, times(1)).findAll((Pageable) any());
    }
}