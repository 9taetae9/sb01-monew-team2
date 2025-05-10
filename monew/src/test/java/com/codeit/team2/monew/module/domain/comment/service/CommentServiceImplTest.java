package com.codeit.team2.monew.module.domain.comment.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.codeit.team2.monew.module.domain.article.entity.Article;
import com.codeit.team2.monew.module.domain.article.exception.ArticleNotFoundException;
import com.codeit.team2.monew.module.domain.article.repository.ArticleRepository;
import com.codeit.team2.monew.module.domain.comment.dto.CommentDto;
import com.codeit.team2.monew.module.domain.comment.dto.CommentOrderBy;
import com.codeit.team2.monew.module.domain.comment.dto.CommentRegisterRequest;
import com.codeit.team2.monew.module.domain.comment.dto.CommentUpdateRequest;
import com.codeit.team2.monew.module.domain.comment.dto.CursorPageRequestCommentDto;
import com.codeit.team2.monew.module.domain.comment.dto.CursorPageResponseCommentDto;
import com.codeit.team2.monew.module.domain.comment.entity.Comment;
import com.codeit.team2.monew.module.domain.comment.exception.CommentNotFoundException;
import com.codeit.team2.monew.module.domain.comment.exception.CommentPermissionDeniedException;
import com.codeit.team2.monew.module.domain.comment.mapper.CommentMapper;
import com.codeit.team2.monew.module.domain.comment.repository.CommentLikeRepository;
import com.codeit.team2.monew.module.domain.comment.repository.CommentRepository;
import com.codeit.team2.monew.module.domain.user.entity.User;
import com.codeit.team2.monew.module.domain.user.exception.UserNotFoundException;
import com.codeit.team2.monew.module.domain.user.repository.UserRepository;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.data.domain.Sort.Direction;

@ExtendWith(MockitoExtension.class)
class CommentServiceImplTest {

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ArticleRepository articleRepository;

    @Mock
    private CommentMapper commentMapper;
    @Mock
    private CommentLikeRepository commentLikeRepository;

    @Spy
    private ApplicationEventPublisher publisher;

    @InjectMocks
    private CommentServiceImpl commentService;

    private UUID userId;
    private UUID articleId;
    private UUID commentId;
    private User user;
    private Article article;
    private Comment comment;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        articleId = UUID.randomUUID();
        commentId = UUID.randomUUID();

        user = mock(User.class);
        article = mock(Article.class);
        comment = mock(Comment.class);
    }

    @Test
    @DisplayName("댓글 등록 - 성공")
    void register_Success() {
        //given
        CommentRegisterRequest request = new CommentRegisterRequest(articleId, userId,
            "test comment");

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(articleRepository.findById(articleId)).thenReturn(Optional.of(article));
        when(commentMapper.toEntity(eq(request), eq(article), eq(user))).thenReturn(comment);
        when(commentRepository.save(comment)).thenReturn(comment);

        //when
        Comment result = commentService.register(request);

        //then
        assertThat(result).isEqualTo(comment);
        verify(userRepository).findById(userId);
        verify(articleRepository).findById(articleId);
        verify(commentMapper).toEntity(eq(request), eq(article), eq(user));
        verify(commentRepository).save(comment);
    }

    @Test
    @DisplayName("댓글 등록 - 실패: 사용자 없음")
    void register_UserNotFound() {
        //given
        CommentRegisterRequest request = new CommentRegisterRequest(articleId, userId,
            "test comment");

        //when
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        //then
        assertThatThrownBy(() -> commentService.register(request))
            .isInstanceOf(UserNotFoundException.class)
            .hasFieldOrPropertyWithValue("errorCode.httpStatus.value", 404);
    }

    @Test
    @DisplayName("댓글 등록 - 실패: 기사 없음")
    void register_ArticleNotFound() {
        //given
        CommentRegisterRequest request = new CommentRegisterRequest(articleId, userId,
            "test comment");

        //when
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(articleRepository.findById(articleId)).thenReturn(Optional.empty());

        //then
        assertThatThrownBy(() -> commentService.register(request))
            .isInstanceOf(ArticleNotFoundException.class)
            .hasFieldOrPropertyWithValue("errorCode.httpStatus.value", 404);
    }

    @Test
    @DisplayName("댓글 수정 - 성공")
    void edit_Success() {
        // given
        CommentUpdateRequest request = new CommentUpdateRequest("edited comment");
        CommentDto expectedDto = new CommentDto(
            commentId,
            articleId,
            userId,
            "testUser",
            "edited comment",
            0L,
            false,
            Instant.now()
        );

        when(commentRepository.findByIdAndDeletedFalse(commentId)).thenReturn(Optional.of(comment));
        when(comment.getUser()).thenReturn(user);
        when(user.getId()).thenReturn(userId);
        when(comment.getId()).thenReturn(commentId);

        when(commentLikeRepository.existsByCommentIdAndUserId(commentId, userId)).thenReturn(false);

        when(commentMapper.toDto(comment, false)).thenReturn(expectedDto);

        // when
        CommentDto result = commentService.edit(commentId, userId, request);

        // then
        assertThat(result).isEqualTo(expectedDto);
        verify(commentRepository).findByIdAndDeletedFalse(commentId);
        verify(comment).update(request.content());
        verify(commentMapper).toDto(comment, false);
    }

    @Test
    @DisplayName("댓글 수정 - 실패: 작성자 아님")
    void edit_Permission_Denied() {
        //given
        CommentUpdateRequest request = new CommentUpdateRequest("edited comment");

        when(commentRepository.findByIdAndDeletedFalse(commentId)).thenReturn(Optional.of(comment));
        when(comment.getUser()).thenReturn(user);
        when(user.getId()).thenReturn(userId);

        assertThatThrownBy(() -> commentService.edit(commentId, UUID.randomUUID(), request))
            .isInstanceOf(CommentPermissionDeniedException.class)
            .hasFieldOrPropertyWithValue("errorCode.httpStatus.value", 403);

    }

    @Test
    @DisplayName("댓글 수정 - 실패: 댓글 없음")
    void edit_Not_Found() {
        CommentUpdateRequest request = new CommentUpdateRequest("edited comment");

        when(commentRepository.findByIdAndDeletedFalse(commentId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> commentService.edit(commentId, userId, request))
            .isInstanceOf(CommentNotFoundException.class)
            .hasFieldOrPropertyWithValue("errorCode.httpStatus.value", 404);
    }

    @Test
    @DisplayName("댓글 삭제 - 성공")
    void delete_success() {
        when(commentRepository.findByIdAndDeletedFalse(commentId)).thenReturn(Optional.of(comment));
        when(comment.getUser()).thenReturn(user);
        when(user.getId()).thenReturn(userId);

        commentService.delete(commentId, userId);

        verify(commentRepository).findByIdAndDeletedFalse(commentId);
        verify(comment).delete();
    }

    @Test
    @DisplayName("댓글 삭제 - 실패: 댓글 작성자 아닐때")
    void delete_Permission_Denied() {
        //given
        when(commentRepository.findByIdAndDeletedFalse(commentId)).thenReturn(Optional.of(comment));
        when(comment.getUser()).thenReturn(user);
        when(user.getId()).thenReturn(userId);

        //when
        //then
        assertThatThrownBy(() -> commentService.delete(commentId, UUID.randomUUID()))
            .isInstanceOf(CommentPermissionDeniedException.class)
            .hasFieldOrPropertyWithValue("errorCode.httpStatus.value", 403);
    }

    @Test
    @DisplayName("댓글 물리 삭제 - 성공")
    void hardDelete_success() {
        //given
        when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));

        //when
        commentService.hardDelete(commentId);

        //then
        verify(commentRepository).findById(commentId);
        verify(commentRepository).delete(comment);
    }

    @Test
    @DisplayName("댓글 물리 삭제 - 실패: 댓글 없음")
    void hardDelete_Comment_Not_Found() {
        //given
        when(commentRepository.findById(commentId)).thenReturn(Optional.empty());

        //when
        //then
        assertThatThrownBy(() -> commentService.hardDelete(commentId))
            .isInstanceOf(CommentNotFoundException.class)
            .hasFieldOrPropertyWithValue("errorCode.httpStatus.value", 404);
    }


    @Test
    @DisplayName("댓글 목록 조회 - 성공")
    void findAll() {
        // given
        CursorPageRequestCommentDto cursorPageRequestCommentDto = new CursorPageRequestCommentDto(
            articleId, CommentOrderBy.createdAt, Direction.ASC, null, null, 10);

        Slice<Comment> slices = new SliceImpl<>(List.of(comment), PageRequest.of(0, 10), false);

        when(userRepository.existsById(any())).thenReturn(true);
        when(commentRepository.findAll(cursorPageRequestCommentDto.articleId(),
            cursorPageRequestCommentDto.orderBy(), cursorPageRequestCommentDto.direction(),
            cursorPageRequestCommentDto.cursor(), cursorPageRequestCommentDto.after(),
            cursorPageRequestCommentDto.limit())).thenReturn(slices);
        when(commentRepository.countByArticleId(articleId)).thenReturn(1L);

        // when
        CursorPageResponseCommentDto result = commentService.findAll(userId,
            cursorPageRequestCommentDto);

        // then
        assertThat(result.content()).hasSize(1);
        assertThat(result.totalElements()).isEqualTo(1L);
        assertThat(result.hasNext()).isEqualTo(false);
        assertThat(result.nextCursor()).isNull();
        assertThat(result.nextAfter()).isNull();
    }
}
