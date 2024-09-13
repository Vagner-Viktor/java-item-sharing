package itemsharing.item.dto;

import itemsharing.booking.dto.BookingDateInfoDto;
import itemsharing.comment.dto.CommentDto;
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
class ItemInfoDtoTest {
    private final JacksonTester<ItemInfoDto> json;

    @Test
    void testSerialize() throws Exception {
        BookingDateInfoDto bookingDateInfoDto = BookingDateInfoDto.builder()
                .id(1L)
                .start(LocalDateTime.now())
                .end(LocalDateTime.now().plusDays(1))
                .bookerId(1L)
                .build();
        CommentDto commentDto = CommentDto.builder()
                .id(1L)
                .created(LocalDateTime.now())
                .authorName("TestCommentAuthorName")
                .text("TestTextComment")
                .build();
        ItemInfoDto itemInfoDto = ItemInfoDto.builder()
                .id(1L)
                .requestId(1L)
                .available(true)
                .description("TestItemDescription")
                .name("TestItemName")
                .lastBooking(bookingDateInfoDto)
                .nextBooking(bookingDateInfoDto)
                .comments(List.of(commentDto))
                .build();

        JsonContent<ItemInfoDto> result = json.write(itemInfoDto);

        assertThat(result).hasJsonPath("$.id")
                .hasJsonPath("$.requestId")
                .hasJsonPath("$.description")
                .hasJsonPath("$.available")
                .hasJsonPath("$.name")
                .hasJsonPath("$.lastBooking.id")
                .hasJsonPath("$.lastBooking.bookerId")
                .hasJsonPath("$.lastBooking.start")
                .hasJsonPath("$.lastBooking.end")
                .hasJsonPath("$.nextBooking.id")
                .hasJsonPath("$.nextBooking.bookerId")
                .hasJsonPath("$.nextBooking.start")
                .hasJsonPath("$.nextBooking.end")
                .hasJsonPath("$.comments[0].id")
                .hasJsonPath("$.comments[0].authorName")
                .hasJsonPath("$.comments[0].text")
                .hasJsonPath("$.comments[0].created");

        assertThat(result).extractingJsonPathNumberValue("$.id")
                .satisfies(id -> assertThat(id.longValue()).isEqualTo(itemInfoDto.getId()));
        assertThat(result).extractingJsonPathNumberValue("$.requestId")
                .satisfies(requestId -> assertThat(requestId.longValue()).isEqualTo(itemInfoDto.getRequestId()));
        assertThat(result).extractingJsonPathStringValue("$.name")
                .satisfies(item_name -> assertThat(item_name).isEqualTo(itemInfoDto.getName()));
        assertThat(result).extractingJsonPathStringValue("$.description")
                .satisfies(item_description -> assertThat(item_description).isEqualTo(itemInfoDto.getDescription()));
        assertThat(result).extractingJsonPathBooleanValue("$.available")
                .satisfies(item_available -> assertThat(item_available).isEqualTo(itemInfoDto.getAvailable()));

        assertThat(result).extractingJsonPathNumberValue("$.lastBooking.id")
                .satisfies(id -> assertThat(id.longValue()).isEqualTo(itemInfoDto.getLastBooking().getId()));
        assertThat(result).extractingJsonPathNumberValue("$.lastBooking.bookerId")
                .satisfies(requestId -> assertThat(requestId.longValue()).isEqualTo(itemInfoDto.getLastBooking().getBookerId()));
        assertThat(result).extractingJsonPathStringValue("$.lastBooking.start")
                .satisfies(created -> assertThat(created).isNotNull());
        assertThat(result).extractingJsonPathStringValue("$.lastBooking.end")
                .satisfies(created -> assertThat(created).isNotNull());

        assertThat(result).extractingJsonPathNumberValue("$.nextBooking.id")
                .satisfies(id -> assertThat(id.longValue()).isEqualTo(itemInfoDto.getNextBooking().getId()));
        assertThat(result).extractingJsonPathNumberValue("$.nextBooking.bookerId")
                .satisfies(requestId -> assertThat(requestId.longValue()).isEqualTo(itemInfoDto.getNextBooking().getBookerId()));
        assertThat(result).extractingJsonPathStringValue("$.nextBooking.start")
                .satisfies(created -> assertThat(created).isNotNull());
        assertThat(result).extractingJsonPathStringValue("$.nextBooking.end")
                .satisfies(created -> assertThat(created).isNotNull());

        assertThat(result).extractingJsonPathNumberValue("$.comments[0].id")
                .satisfies(item_id -> assertThat(item_id.longValue()).isEqualTo(itemInfoDto.getComments().iterator().next().getId()));
        assertThat(result).extractingJsonPathStringValue("$.comments[0].authorName")
                .satisfies(item_name -> assertThat(item_name).isEqualTo(itemInfoDto.getComments().iterator().next().getAuthorName()));
        assertThat(result).extractingJsonPathStringValue("$.comments[0].text")
                .satisfies(item_description -> assertThat(item_description).isEqualTo(itemInfoDto.getComments().iterator().next().getText()));
        assertThat(result).extractingJsonPathStringValue("$.comments[0].created")
                .satisfies(created -> assertThat(created).isNotNull());
    }
}