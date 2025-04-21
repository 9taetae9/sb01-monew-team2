package com.codeit.team2.monew.module.domain.interest.service;

import com.codeit.team2.monew.module.domain.interest.controller.InterestRegisterRequest;
import com.codeit.team2.monew.module.domain.interest.dto.InterestDto;
import com.codeit.team2.monew.module.domain.interest.entity.Interest;
import com.codeit.team2.monew.module.domain.interest.entity.Keyword;
import com.codeit.team2.monew.module.domain.interest.mapper.InterestMapper;
import com.codeit.team2.monew.module.domain.interest.repository.InterestRepository;
import com.codeit.team2.monew.module.domain.interest.repository.KeywordRepository;
import com.codeit.team2.monew.module.domain.subscription.repository.SubscriptionRepository;
import com.codeit.team2.monew.module.domain.user.entity.User;
import com.codeit.team2.monew.module.domain.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class InterestService {

    private final UserRepository userRepository;
    private final InterestRepository interestRepository;
    private final KeywordRepository keywordRepository;
    private final SubscriptionRepository subscriptionRepository;

    @Transactional
    public InterestDto create(InterestRegisterRequest request, String userId) {

        User user = findByIdOrThrow(userId);

        // TODO: 관심사 이름 유사도 검사 - 80% 이상 일치 시 등록 불가

        Interest interest = Interest.create(request.name());

        // 키워드 확인 후 추가
        for (String keyword : request.keywords()) {
            Keyword getKeyword = keywordRepository.findByName(keyword)
                .orElse(new Keyword(keyword));
            interest.addInterestKeyword(getKeyword);
        }

        Interest savedInterest = interestRepository.save(interest);

        List<String> keywords = savedInterest.getKeywords().stream()
            .map(ik -> ik.getKeyword().getName())
            .collect(Collectors.toList());


        // 초기 생성 시에는 구독하고 있지 않음, 생성 시 구독으로 처리할 건지?
        boolean subscribedByMe = false;

        return InterestMapper.INSTANCE.toDto(savedInterest, keywords, subscribedByMe);
    }

    private User findByIdOrThrow(String stringUserId) {
        UUID userId;
        try {
            userId = UUID.fromString(stringUserId);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("유효한 id 형식이 아닙니다.");
        }

        User user = userRepository.findById(userId).orElseThrow(
            () -> new RuntimeException("user not found"));
        return user;
    }

}
