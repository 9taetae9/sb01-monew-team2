package com.codeit.team2.monew.module.domain.useractivity.service;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.codeit.team2.monew.module.domain.article.entity.Article;
import com.codeit.team2.monew.module.domain.article.entity.ArticleView;
import com.codeit.team2.monew.module.domain.article.repository.ArticleViewRepository;
import com.codeit.team2.monew.module.domain.comment.entity.Comment;
import com.codeit.team2.monew.module.domain.comment.entity.CommentLike;
import com.codeit.team2.monew.module.domain.comment.repository.CommentLikeRepository;
import com.codeit.team2.monew.module.domain.comment.repository.CommentRepository;
import com.codeit.team2.monew.module.domain.interest.entity.Interest;
import com.codeit.team2.monew.module.domain.interest.entity.InterestKeyword;
import com.codeit.team2.monew.module.domain.interest.entity.Keyword;
import com.codeit.team2.monew.module.domain.subscription.entity.Subscription;
import com.codeit.team2.monew.module.domain.subscription.repository.SubscriptionRepository;
import com.codeit.team2.monew.module.domain.user.entity.User;
import com.codeit.team2.monew.module.domain.user.repository.UserRepository;
import com.codeit.team2.monew.module.domain.useractivity.dto.UserActivityDto;
import com.codeit.team2.monew.module.domain.useractivity.mapper.UserActivityMapper;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UserActivityServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private SubscriptionRepository subscriptionRepository;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private CommentLikeRepository commentLikeRepository;

    @Mock
    private ArticleViewRepository articleViewRepository;

    @Spy
    private UserActivityMapper userActivitiesMapper = Mappers.getMapper(
        UserActivityMapper.class);

    @InjectMocks
    private UserActivityServiceImpl userActivitiesService;

    @Test
    void 사용자_활동_내역_조회_성공() {
        // given
        // User
        UUID userId = UUID.randomUUID();
        UUID loginId = userId;
        User mockUser = mock(User.class);
        when(mockUser.getId()).thenReturn(userId);
        when(mockUser.getEmail()).thenReturn("test@example.com");
        when(mockUser.getNickname()).thenReturn("nickname");
        when(mockUser.getCreatedAt()).thenReturn(Instant.now());

        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));

        // Subscription
        Subscription mockSubscription = mock(Subscription.class);
        Interest mockInterest = mock(Interest.class);
        Keyword mockKeyword = mock(Keyword.class);
        InterestKeyword mockInterestKeyword = mock(InterestKeyword.class);

        when(mockSubscription.getInterest()).thenReturn(mockInterest);
        when(mockSubscription.getCreatedAt()).thenReturn(Instant.now());
        when(mockSubscription.getId()).thenReturn(UUID.randomUUID());

        when(mockInterest.getId()).thenReturn(UUID.randomUUID());
        when(mockInterest.getName()).thenReturn("경제");
        when(mockInterest.getSubscriberCount()).thenReturn(20);
        when(mockInterest.getKeywords()).thenReturn(List.of(mockInterestKeyword));
        when(mockInterestKeyword.getKeyword()).thenReturn(mockKeyword);
        when(mockKeyword.getName()).thenReturn("금리");

        when(subscriptionRepository.findAllByUserOrderByCreatedAtDesc(mockUser)).thenReturn(
            List.of(mockSubscription));

        // Comment
        Article mockArticle = mock(Article.class);
        Comment mockComment = mock(Comment.class);
        when(mockComment.getId()).thenReturn(UUID.randomUUID());
        when(mockComment.getContent()).thenReturn("댓글 내용");
        when(mockComment.getLikeCount()).thenReturn(3L);
        when(mockComment.getCreatedAt()).thenReturn(Instant.now());
        when(mockComment.getUser()).thenReturn(mockUser);
        when(mockComment.getArticle()).thenReturn(mockArticle);
        when(mockArticle.getId()).thenReturn(UUID.randomUUID());
        when(mockArticle.getTitle()).thenReturn("기사 제목");

        when(commentRepository.findTop10ByUserOrderByCreatedAtDesc(mockUser)).thenReturn(
            List.of(mockComment));

        // CommentLike
        CommentLike mockLike = mock(CommentLike.class);
        when(mockLike.getId()).thenReturn(UUID.randomUUID());
        when(mockLike.getCreatedAt()).thenReturn(Instant.now());
        when(mockLike.getComment()).thenReturn(mockComment);

        when(commentLikeRepository.findTop10ByUserOrderByLikedAtDesc(mockUser)).thenReturn(
            List.of(mockLike));

        // ArticleView
        ArticleView mockView = mock(ArticleView.class);
        when(mockView.getId()).thenReturn(UUID.randomUUID());
        when(mockView.getUser()).thenReturn(mockUser);
        when(mockView.getCreatedAt()).thenReturn(Instant.now());
        when(mockView.getArticle()).thenReturn(mockArticle);

        when(mockArticle.getSource()).thenReturn("연합뉴스");
        when(mockArticle.getSourceUrl()).thenReturn("https://...");
        when(mockArticle.getPublishedDate()).thenReturn(Instant.now());
        when(mockArticle.getSummary()).thenReturn("요약");
        when(mockArticle.getViewCount()).thenReturn(100);

        when(articleViewRepository.findTop10ByUserOrderByViewedAtDesc(mockUser)).thenReturn(
            List.of(mockView));
        when(commentRepository.countByArticle(mockArticle)).thenReturn(5L);

        // when
        UserActivityDto userActivityDto = userActivitiesService.findUserActivities(loginId, userId);

        // then
        Assertions.assertEquals(userId, userActivityDto.id());
    }

}
