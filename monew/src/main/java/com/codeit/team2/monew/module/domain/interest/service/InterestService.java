package com.codeit.team2.monew.module.domain.interest.service;

import com.codeit.team2.monew.module.domain.interest.dto.request.InterestRegisterRequest;
import com.codeit.team2.monew.module.domain.interest.dto.request.InterestUpdateRequest;
import com.codeit.team2.monew.module.domain.interest.dto.response.InterestDto;
import com.codeit.team2.monew.module.domain.interest.entity.Interest;
import com.codeit.team2.monew.module.domain.interest.entity.Keyword;
import com.codeit.team2.monew.module.domain.interest.mapper.InterestMapper;
import com.codeit.team2.monew.module.domain.interest.repository.InterestRepository;
import com.codeit.team2.monew.module.domain.interest.repository.KeywordRepository;
import com.codeit.team2.monew.module.domain.subscription.repository.SubscriptionRepository;
import com.codeit.team2.monew.module.domain.user.entity.User;
import com.codeit.team2.monew.module.domain.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
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
    public InterestDto create(InterestRegisterRequest request, UUID userId) {

        User user = findByIdOrThrow(userId);

        // TODO: 추후에 index 추가 예정
        // 참고: pg_trgm 특성상 유사도 계산 알고리즘이 달라 사람이 판단하는 것과 다름. 보완 필요
        if(interestRepository.existsByNameSimilarTo(request.name())) {
            throw new IllegalArgumentException("비슷한 관심사가 이미 존재합니다.");
        }

        Interest interest = Interest.create(request.name());

        for (String keyword : request.keywords()) {
            Keyword getKeyword = keywordRepository.findByName(keyword)
                .orElseGet(() -> keywordRepository.save(new Keyword(keyword)));
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

    private UUID convertUUID(String userId) {
        try {
            return UUID.fromString(userId);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("UUID 형식이 아닙니다.");
        }
    }

    private User findByIdOrThrow(UUID userId) {
        User user = userRepository.findById(userId).orElseThrow(
            () -> new RuntimeException("user not found"));
        return user;
    }

    public InterestDto update(InterestUpdateRequest request, UUID interestId, UUID userId) {



        return null;
    }
}
