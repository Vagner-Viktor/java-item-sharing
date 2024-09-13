package itemsharing.comment;

import itemsharing.comment.dto.CommentDto;
import itemsharing.comment.model.Comment;

import java.util.Collection;
import java.util.List;

public class CommentMapper {
    public static CommentDto toCommentDto(Comment comment) {
        if (comment == null) return null;
        return CommentDto.builder()
                .id(comment.getId())
                .text(comment.getText())
                .authorName(comment.getAuthor().getName())
                .created(comment.getCreated())
                .build();
    }

    public static Collection<CommentDto> toCommentsDtoCollection(Collection<Comment> comments) {
        if (comments == null) return List.of();
        return comments.stream()
                .map(CommentMapper::toCommentDto)
                .toList();
    }
}
