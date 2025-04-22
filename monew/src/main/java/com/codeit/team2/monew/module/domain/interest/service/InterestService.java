package com.codeit.team2.monew.module.domain.interest.service;

import com.codeit.team2.monew.module.domain.interest.dto.request.InterestRegisterRequest;
import com.codeit.team2.monew.module.domain.interest.dto.request.InterestUpdateRequest;
import com.codeit.team2.monew.module.domain.interest.dto.response.InterestDto;
import com.codeit.team2.monew.module.domain.interest.entity.Interest;
import com.codeit.team2.monew.module.domain.interest.entity.InterestKeyword;
import com.codeit.team2.monew.module.domain.interest.entity.Keyword;
import com.codeit.team2.monew.module.domain.interest.mapper.InterestMapper;
import com.codeit.team2.monew.module.domain.interest.repository.InterestRepository;
import com.codeit.team2.monew.module.domain.interest.repository.KeywordRepository;
import com.codeit.team2.monew.module.domain.subscription.repository.SubscriptionRepository;
import com.codeit.team2.monew.module.domain.user.entity.User;
import com.codeit.team2.monew.module.domain.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
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

        User user = findUserOrThrow(userId);

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

        boolean subscribedByMe = false;

        return InterestMapper.INSTANCE.toDto(savedInterest, keywords, subscribedByMe);
    }

    @Transactional
    public InterestDto update(InterestUpdateRequest request, UUID id, UUID userId) {

        findUserOrThrow(userId);
        Interest interest = findByIdOrThrow(id);

        // 키워드 이름들을 가져와서 포함되어 있으면 그대로 두고 없으면 제거 시키는 키워드 (키워드가 고아가 되면 삭제되도록?)

        Map<String, InterestKeyword> savedKeywords = interest.getKeywords().stream()
            .collect(Collectors.toMap(
                ik -> ik.getKeyword().toString(),  // key
                ik-> ik     // value
            ));

        for (String keyword: request.keywords()) {
            if (!savedKeywords.containsKey(keyword)) { // 새로운 키워드인 경우
                Keyword getKeyword = keywordRepository.findByName(keyword)
                    .orElseGet(() -> keywordRepository.save(new Keyword(keyword)));
                interest.addInterestKeyword(getKeyword);
            } else {
                savedKeywords.remove(keyword);
            }
        }

        // 남아있다면, remove 하기
        if (!savedKeywords.isEmpty()) {
            interest.getKeywords().removeAll(savedKeywords.values());
        }

        List<String> keywords = interest.getKeywords().stream()
            .map(ik -> ik.getKeyword().getName())
            .collect(Collectors.toList());

        // TODO : 구독 확인

        return InterestMapper.INSTANCE.toDto(interest, keywords, true);
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
