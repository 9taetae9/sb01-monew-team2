package com.codeit.team2.monew.module.domain.comment.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.codeit.team2.monew.module.domain.article.entity.Article;
import com.codeit.team2.monew.module.domain.article.repository.ArticleRepository;
import com.codeit.team2.monew.module.domain.comment.dto.CommentRegisterRequest;
import com.codeit.team2.monew.module.domain.comment.entity.Comment;
import com.codeit.team2.monew.module.domain.comment.mapper.CommentMapper;
import com.codeit.team2.monew.module.domain.comment.repository.CommentRepository;
import com.codeit.team2.monew.module.domain.user.entity.User;
import com.codeit.team2.monew.module.domain.user.repository.UserRepository;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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
            .isInstanceOf(IllegalArgumentException.class);
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
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("댓글 삭제 - 성공")
    void delete_success() {
        //given
        when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));
        when(comment.getUser()).thenReturn(user);
        when(user.getId()).thenReturn(userId);

        //when
        commentService.delete(commentId, userId);

        //then
        verify(commentRepository).findById(commentId);
        verify(comment).delete();
    }
}
