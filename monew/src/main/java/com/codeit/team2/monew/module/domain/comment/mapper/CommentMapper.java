package com.codeit.team2.monew.module.domain.comment.mapper;

import com.codeit.team2.monew.module.domain.article.entity.Article;
import com.codeit.team2.monew.module.domain.comment.dto.CommentDto;
import com.codeit.team2.monew.module.domain.comment.dto.CommentLikeDto;
import com.codeit.team2.monew.module.domain.comment.dto.CommentRegisterRequest;
import com.codeit.team2.monew.module.domain.comment.entity.Comment;
import com.codeit.team2.monew.module.domain.comment.entity.CommentLike;
import com.codeit.team2.monew.module.domain.user.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(
    componentModel = "spring",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface CommentMapper {

    @Mapping(source = "comment.article.id", target = "articleId")
    @Mapping(source = "comment.user.id", target = "userId")
    @Mapping(source = "comment.user.nickname", target = "userNickname")
    @Mapping(source = "likedByMe", target = "likedByMe")
    CommentDto toDto(Comment comment, boolean likedByMe);

    @Mapping(source = "user.id", target = "likedBy")
    @Mapping(source = "comment.id", target = "commentId")
    @Mapping(source = "comment.article.id", target = "articleId")
    @Mapping(source = "comment.user.id", target = "commentUserId")
    @Mapping(source = "comment.user.nickname", target = "commentUserNickname")
    @Mapping(source = "comment.content", target = "commentContent")
    @Mapping(source = "comment.likeCount", target = "commentLikeCount")
    @Mapping(source = "comment.createdAt", target = "commentCreatedAt")
    @Mapping(source = "likedAt", target = "createdAt")
    CommentLikeDto toDto(CommentLike commentLike);


    default Comment toEntity(CommentRegisterRequest request, Article article, User user) {
        return Comment.create(article, user, request.content());
    }
}
