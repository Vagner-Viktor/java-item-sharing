package itemsharing.item;

import itemsharing.comment.dto.CommentDto;
import itemsharing.comment.dto.CommentRequestDto;
import itemsharing.item.dto.ItemDto;
import itemsharing.item.dto.ItemInfoDto;

import java.util.Collection;

public interface ItemService {
    Collection<ItemDto> findAll();

    ItemDto create(Long userId, ItemDto itemDto);

    ItemDto update(Long userId, Long itemId, ItemDto itemDto);

    CommentDto addComment(Long userId, Long itemId, CommentRequestDto commentRequestDto);

    ItemInfoDto findItemById(Long userId, Long itemId);

    Collection<ItemInfoDto> findItemsByUserId(Long userId);

    void delete(Long itemId);

    Collection<ItemDto> findItemsByText(String text);
}
