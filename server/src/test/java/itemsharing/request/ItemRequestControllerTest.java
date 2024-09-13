package itemsharing.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import itemsharing.request.dto.ItemRequestDto;
import itemsharing.request.dto.ItemRequestRequestDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemRequestController.class)
@AutoConfigureMockMvc
class ItemRequestControllerTest {

    @MockBean
    private ItemRequestService itemRequestService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    public ItemRequestDto getItemRequestDto() {
        return ItemRequestDto.builder()
                .id(1L)
                .description("TestItemRequestDescription")
                .requestor(null)
                .created(LocalDateTime.now())
                .items(List.of())
                .build();
    }

    @Test
    void create() throws Exception {
        // given
        ItemRequestDto itemRequestDto = getItemRequestDto();
        ItemRequestRequestDto itemRequestRequestDto = new ItemRequestRequestDto(1L, "TestItemRequestRequestDescription");
        when(itemRequestService.create(any())).thenReturn(itemRequestDto);

        // when + then
        mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(itemRequestRequestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemRequestDto.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(itemRequestDto.getDescription())))
                .andExpect(jsonPath("$.created", is(itemRequestDto.getCreated().format(DateTimeFormatter.ISO_DATE_TIME))));
        verify(itemRequestService, times(1)).create(any());
    }

    @Test
    void findAll() throws Exception {
        // given
        when(itemRequestService.findAllByUserId(anyLong())).thenReturn(Collections.emptyList());

        // when + then
        mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
        verify(itemRequestService, times(1)).findAllByUserId(anyLong());
    }

    @Test
    void findItemRequestById() throws Exception {
        // given
        ItemRequestDto itemRequestDto = getItemRequestDto();
        when(itemRequestService.findItemRequestById(anyLong())).thenReturn(itemRequestDto);

        // when + then
        mockMvc.perform(get("/requests/1")
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemRequestDto.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(itemRequestDto.getDescription())))
                .andExpect(jsonPath("$.created", is(itemRequestDto.getCreated().format(DateTimeFormatter.ISO_DATE_TIME))));
        verify(itemRequestService, times(1)).findItemRequestById(anyLong());
    }

    @Test
    void findAllUsersItemRequest() throws Exception {
        // given
        when(itemRequestService.findAllUsersItemRequest(any())).thenReturn(Collections.emptyList());

        // when + then
        mockMvc.perform(get("/requests/all?from=0&size=10")
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
        verify(itemRequestService, times(1)).findAllUsersItemRequest(any());
    }
}