package com.codeit.team2.monew.module.domain.useractivity.service;

import com.codeit.team2.monew.module.domain.article.entity.ArticleView;
import com.codeit.team2.monew.module.domain.article.repository.ArticleViewRepository;
import com.codeit.team2.monew.module.domain.comment.entity.Comment;
import com.codeit.team2.monew.module.domain.comment.entity.CommentLike;
import com.codeit.team2.monew.module.domain.comment.repository.CommentLikeRepository;
import com.codeit.team2.monew.module.domain.comment.repository.CommentRepository;
import com.codeit.team2.monew.module.domain.subscription.entity.Subscription;
import com.codeit.team2.monew.module.domain.subscription.repository.SubscriptionRepository;
import com.codeit.team2.monew.module.domain.user.entity.User;
import com.codeit.team2.monew.module.domain.user.repository.UserRepository;
import com.codeit.team2.monew.module.domain.useractivity.dto.ArticleViewItem;
import com.codeit.team2.monew.module.domain.useractivity.dto.CommentItem;
import com.codeit.team2.monew.module.domain.useractivity.dto.CommentLikeItem;
import com.codeit.team2.monew.module.domain.useractivity.dto.SubscriptionItem;
import com.codeit.team2.monew.module.domain.useractivity.dto.UserActivityDto;
import com.codeit.team2.monew.module.domain.useractivity.mapper.UserActivityMapper;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserActivityServiceImpl implements UserActivityService {

    private final UserRepository userRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final CommentRepository commentRepository;
    private final CommentLikeRepository commentLikeRepository;
    private final ArticleViewRepository articleViewRepository;
    private final UserActivityMapper userActivityMapper;

    @Override
    @Transactional(readOnly = true)
    public UserActivityDto findUserActivities(UUID loginId, UUID userId) {
        if (!loginId.equals(userId)) {
            throw new RuntimeException("Not Authorized");
        }

        // 사용자
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("Not Found User"));

        // 구독 중인 관심사
        List<Subscription> subscriptions =
            subscriptionRepository.findAllByUserOrderByCreatedAtDesc(user);
        List<SubscriptionItem> subscriptionItems = subscriptions.stream().map(
            subscription -> new SubscriptionItem(
                subscription.getId(),
                subscription.getInterest().getId(),
                subscription.getInterest().getName(),
                subscription.getInterest().getKeywords().stream().map(
                    interestKeyword -> interestKeyword.getKeyword().getName()
                ).collect(Collectors.toList()),
                (long) subscription.getInterest().getSubscriberCount(),
                subscription.getCreatedAt()
            )
        ).toList();

        // 최근 작성한 댓글
        List<Comment> comments = commentRepository.findTop10ByUserOrderByCreatedAtDesc(user);
        List<CommentItem> commentItems = comments.stream().map(
            comment -> new CommentItem(
                comment.getId(),
                comment.getArticle().getId(),
                comment.getArticle().getTitle(),
                user.getId(),
                user.getNickname(),
                comment.getContent(),
                comment.getLikeCount(),
                comment.getCreatedAt()
            )
        ).toList();

        // 최근 좋아요 누른 댓글
        List<CommentLike> commentLikes =
            commentLikeRepository.findTop10ByUserOrderByLikedAtDesc(user);
        List<CommentLikeItem> commentLikeItems = commentLikes.stream().map(
            commentLike -> new CommentLikeItem(
                commentLike.getId(),
                commentLike.getCreatedAt(),
                commentLike.getComment().getId(),
                commentLike.getComment().getArticle().getId(),
                commentLike.getComment().getArticle().getTitle(),
                commentLike.getComment().getUser().getId(),
                commentLike.getComment().getUser().getNickname(),
                commentLike.getComment().getContent(),
                commentLike.getComment().getLikeCount(),
                commentLike.getComment().getCreatedAt()
            )
        ).toList();

        // 최근 본 뉴스
        List<ArticleView> articleViews =
            articleViewRepository.findTop10ByUserOrderByViewedAtDesc(user);
        List<ArticleViewItem> articleViewItems = articleViews.stream().map(
            articleView -> new ArticleViewItem(
                articleView.getId(),
                articleView.getUser().getId(),
                articleView.getCreatedAt(),
                articleView.getArticle().getId(),
                articleView.getArticle().getSource(),
                articleView.getArticle().getSourceUrl(),
                articleView.getArticle().getTitle(),
                articleView.getArticle().getPublishedDate(),
                articleView.getArticle().getSummary(),
                commentRepository.countByArticle(articleView.getArticle()),
                articleView.getArticle().getViewCount().longValue()
            )
        ).toList();

        return new UserActivityDto(
            user.getId(),
            user.getEmail(),
            user.getNickname(),
            user.getCreatedAt(),
            subscriptionItems,
            commentItems,
            commentLikeItems,
            articleViewItems
        );
    }
}
