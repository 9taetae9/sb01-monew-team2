package com.codeit.team2.monew.module.domain.interest.service;

import com.codeit.team2.monew.module.domain.interest.dto.request.InterestRegisterRequest;
import com.codeit.team2.monew.module.domain.interest.dto.request.InterestUpdateRequest;
import com.codeit.team2.monew.module.domain.interest.dto.response.InterestDto;
import com.codeit.team2.monew.module.domain.interest.entity.Interest;
import com.codeit.team2.monew.module.domain.interest.entity.InterestKeyword;
import com.codeit.team2.monew.module.domain.interest.entity.Keyword;
import com.codeit.team2.monew.module.domain.interest.mapper.InterestMapper;
import com.codeit.team2.monew.module.domain.interest.repository.InterestKeywordRepository;
import com.codeit.team2.monew.module.domain.interest.repository.InterestRepository;
import com.codeit.team2.monew.module.domain.interest.repository.KeywordRepository;
import com.codeit.team2.monew.module.domain.subscription.repository.SubscriptionRepository;
import com.codeit.team2.monew.module.domain.user.entity.User;
import com.codeit.team2.monew.module.domain.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Map;
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
    private final InterestKeywordRepository interestKeywordRepository;
    private final SubscriptionRepository subscriptionRepository;

    @Transactional
    public InterestDto create(InterestRegisterRequest request, UUID userId) {

        User user = findUserOrThrow(userId);
        boolean subscribedByMe = false;

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

        return InterestMapper.INSTANCE.toDto(savedInterest, keywords, subscribedByMe);
    }

    @Transactional
    public InterestDto update(InterestUpdateRequest request, UUID id, UUID userId) {

        User user = findUserOrThrow(userId);
        Interest interest = findByIdOrThrow(id);
        boolean subscribedByMe = subscriptionRepository.existsByInterestAndUser(interest, user);

        Map<String, InterestKeyword> savedKeywords = interest.getKeywords().stream()
            .collect(Collectors.toMap(
                ik -> ik.getKeyword().toString(),
                ik-> ik
            ));

        for (String keyword: request.keywords()) {
            if (!savedKeywords.containsKey(keyword)) {
                Keyword getKeyword = keywordRepository.findByName(keyword)
                    .orElseGet(() -> keywordRepository.save(new Keyword(keyword)));
                interest.addInterestKeyword(getKeyword);
            } else {
                savedKeywords.remove(keyword);
            }
        }

        if (!savedKeywords.isEmpty()) {
            for (InterestKeyword interestKeyword : savedKeywords.values()) {
                interest.getKeywords().remove(interestKeyword);
                Keyword keyword = interestKeyword.getKeyword();
                if (!interestKeywordRepository.existsByKeyword(keyword)) {
                    keywordRepository.delete(keyword);
                }
            }
        }

        List<String> keywords = interest.getKeywords().stream()
            .map(ik -> ik.getKeyword().getName())
            .collect(Collectors.toList());

        return InterestMapper.INSTANCE.toDto(interest, keywords, subscribedByMe);
    }

    private User findUserOrThrow(UUID userId) {
        User user = userRepository.findById(userId).orElseThrow(
            () -> new RuntimeException("user not found"));
        return user;
    }

    private Interest findByIdOrThrow(UUID id) {
        Interest interest = interestRepository.findById(id).orElseThrow(
            () -> new RuntimeException("interest not found"));
        return interest;
    }

}
