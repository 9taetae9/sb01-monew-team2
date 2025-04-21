package com.codeit.team2.monew.module.domain.comment.service;

import com.codeit.team2.monew.module.domain.article.entity.Article;
import com.codeit.team2.monew.module.domain.article.repository.ArticleRepository;
import com.codeit.team2.monew.module.domain.comment.dto.CommentRegisterRequest;
import com.codeit.team2.monew.module.domain.comment.dto.CommentUpdateRequest;
import com.codeit.team2.monew.module.domain.comment.entity.Comment;
import com.codeit.team2.monew.module.domain.comment.mapper.CommentMapper;
import com.codeit.team2.monew.module.domain.comment.repository.CommentRepository;
import com.codeit.team2.monew.module.domain.user.entity.User;
import com.codeit.team2.monew.module.domain.user.repository.UserRepository;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class CommentServiceImpl implements CommentService {

    private final UserRepository userRepository;
    private final ArticleRepository articleRepository;
    private final CommentMapper commentMapper;
    private final CommentRepository commentRepository;

    @Override
    public Comment register(CommentRegisterRequest request) {
        User user = userRepository.findById(request.userId())
            .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        Article article = articleRepository.findById(request.articleId())
            .orElseThrow(() -> new IllegalArgumentException("기사를 찾을 수 없습니다."));

        Comment comment = commentMapper.toEntity(request, article, user);

        return commentRepository.save(comment);
    }

    @Override
    public Comment edit(UUID commentId, UUID userId, CommentUpdateRequest request) {
        return null;
    }

    @Override
    public void delete(UUID commentId, UUID userId) {

    }
}
