package itemsharing.request.dto;

import itemsharing.item.model.Item;
import itemsharing.request.ItemRequestMapper;
import itemsharing.request.model.ItemRequest;
import itemsharing.user.model.User;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemRequestDtoTest {
    private final JacksonTester<ItemRequestDto> json;

    @Test
    void testSerialize() throws Exception {
        User user = User.builder()
                .id(1L)
                .name("TestUserName")
                .email("UserEmail@test.com")
                .build();
        ItemRequest itemRequest = ItemRequest.builder()
                .id(1L)
                .requestor(user)
                .created(LocalDateTime.now())
                .description("TestItemRequestDescription")
                .build();
        Item item = Item.builder()
                .id(1L)
                .name("TestItemName")
                .description("TestItemDescription")
                .request(itemRequest)
                .available(true)
                .owner(user)
                .build();
        itemRequest.setItems(List.of(item));
        ItemRequestDto itemRequestDto = ItemRequestMapper.toItemRequestDto(itemRequest);

        JsonContent<ItemRequestDto> result = json.write(itemRequestDto);

        assertThat(result).hasJsonPath("$.id")
                .hasJsonPath("$.created")
                .hasJsonPath("$.description")
                .hasJsonPath("$.requestor.id")
                .hasJsonPath("$.requestor.name")
                .hasJsonPath("$.requestor.email")
                .hasJsonPath("$.items[0].id")
                .hasJsonPath("$.items[0].name")
                .hasJsonPath("$.items[0].description")
                .hasJsonPath("$.items[0].available");

        assertThat(result).extractingJsonPathNumberValue("$.id")
                .satisfies(id -> assertThat(id.longValue()).isEqualTo(itemRequestDto.getId()));
        assertThat(result).extractingJsonPathStringValue("$.created")
                .satisfies(created -> assertThat(created).isNotNull());
        assertThat(result).extractingJsonPathNumberValue("$.requestor.id")
                .satisfies(requestor_id -> assertThat(requestor_id.longValue()).isEqualTo(itemRequestDto.getId()));
        assertThat(result).extractingJsonPathStringValue("$.requestor.name")
                .satisfies(requestor_name -> assertThat(requestor_name).isEqualTo(itemRequestDto.getRequestor().getName()));
        assertThat(result).extractingJsonPathStringValue("$.requestor.email")
                .satisfies(requestor_email -> assertThat(requestor_email).isEqualTo(itemRequestDto.getRequestor().getEmail()));
        assertThat(result).extractingJsonPathNumberValue("$.items[0].id")
                .satisfies(item_id -> assertThat(item_id.longValue()).isEqualTo(itemRequestDto.getItems().iterator().next().getId()));
        assertThat(result).extractingJsonPathStringValue("$.items[0].name")
                .satisfies(item_name -> assertThat(item_name).isEqualTo(itemRequestDto.getItems().iterator().next().getName()));
        assertThat(result).extractingJsonPathStringValue("$.items[0].description")
                .satisfies(item_description -> assertThat(item_description).isEqualTo(itemRequestDto.getItems().iterator().next().getDescription()));
        assertThat(result).extractingJsonPathBooleanValue("$.items[0].available")
                .satisfies(item_available -> assertThat(item_available).isEqualTo(itemRequestDto.getItems().iterator().next().getAvailable()));
    }
}