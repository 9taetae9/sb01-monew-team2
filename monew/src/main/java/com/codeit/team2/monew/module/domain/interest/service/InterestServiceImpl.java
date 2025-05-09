package com.codeit.team2.monew.module.domain.interest.service;

import com.codeit.team2.monew.module.domain.interest.dto.request.CursorPageRequestInterestDto;
import com.codeit.team2.monew.module.domain.interest.dto.request.InterestOrderBy;
import com.codeit.team2.monew.module.domain.interest.dto.request.InterestRegisterRequest;
import com.codeit.team2.monew.module.domain.interest.dto.request.InterestUpdateRequest;
import com.codeit.team2.monew.module.domain.interest.dto.response.CursorPageResponseInterestDto;
import com.codeit.team2.monew.module.domain.interest.dto.response.InterestDto;
import com.codeit.team2.monew.module.domain.interest.entity.Interest;
import com.codeit.team2.monew.module.domain.interest.entity.InterestKeyword;
import com.codeit.team2.monew.module.domain.interest.entity.Keyword;
import com.codeit.team2.monew.module.domain.interest.event.InterestDeleteEvent;
import com.codeit.team2.monew.module.domain.interest.event.InterestUpdateEvent;
import com.codeit.team2.monew.module.domain.interest.exception.InterestNotFoundException;
import com.codeit.team2.monew.module.domain.interest.exception.SimilarInterestAlreadyExistsException;
import com.codeit.team2.monew.module.domain.interest.mapper.InterestMapper;
import com.codeit.team2.monew.module.domain.interest.repository.InterestKeywordRepository;
import com.codeit.team2.monew.module.domain.interest.repository.InterestRepository;
import com.codeit.team2.monew.module.domain.interest.repository.KeywordRepository;
import com.codeit.team2.monew.module.domain.subscription.repository.SubscriptionRepository;
import com.codeit.team2.monew.module.domain.user.entity.User;
import com.codeit.team2.monew.module.domain.user.exception.UserNotFoundException;
import com.codeit.team2.monew.module.domain.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class InterestServiceImpl implements InterestService {

    private final double SIMILARITY_THRESHOLD = 0.8;

    private final InterestMapper interestMapper;
    private final UserRepository userRepository;
    private final InterestRepository interestRepository;
    private final KeywordRepository keywordRepository;
    private final InterestKeywordRepository interestKeywordRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final ApplicationEventPublisher publisher;
    private final InterestNameSimilarityService interestNameSimilarityService;

    @Override
    @Transactional
    public InterestDto create(InterestRegisterRequest request, UUID userId) {

        User user = getUserOrThrow(userId);
        boolean subscribedByMe = false;

        List<String> savedNames = interestRepository.findAllNames();
        String interestName = request.name();

        for (String savedName : savedNames) {
            if (interestNameSimilarityService.isSimilar(interestName, savedName,
                SIMILARITY_THRESHOLD)) {
                throw new SimilarInterestAlreadyExistsException(interestName);
            }
        }

        Interest interest = Interest.create(interestName);

        Set<String> keywordSet = new HashSet<>(request.keywords());
        for (String keyword : keywordSet) {
            Keyword getKeyword = keywordRepository.findByName(keyword)
                .orElseGet(() -> keywordRepository.save(new Keyword(keyword)));
            interest.addKeyword(getKeyword);
        }

        Interest savedInterest = interestRepository.save(interest);

        List<String> keywords = savedInterest.getKeywords().stream()
            .map(ik -> ik.getKeyword().getName())
            .collect(Collectors.toList());

        return interestMapper.toDto(savedInterest, keywords, subscribedByMe);
    }

    @Override
    @Transactional
    public InterestDto update(InterestUpdateRequest request, UUID id, UUID userId) {

        User user = getUserOrThrow(userId);
        Interest interest = getByIdWithKeywordsOrThrow(id);
        boolean subscribedByMe = subscriptionRepository.existsByInterestAndUser(interest, user);

        updateKeywords(interest, request.keywords());

        List<String> keywords = interest.getKeywords().stream()
            .map(ik -> ik.getKeyword().getName())
            .collect(Collectors.toList());

        // 관심사 수정 이벤트 발생
        publisher.publishEvent(new InterestUpdateEvent(
            interest,
            keywords,
            userId
        ));

        return interestMapper.toDto(interest, keywords, subscribedByMe);
    }

    @Override
    @Transactional
    public void delete(UUID id, UUID userId) {

        getUserOrThrow(userId);
        Interest interest = getByIdOrThrow(id);

        interestRepository.delete(interest);

        keywordRepository.deleteAllOrphanKeywords();

        // 관심사 삭제 이벤트 발생
        publisher.publishEvent(new InterestDeleteEvent(
            interest,
            userId
        ));
    }

    @Override
    public CursorPageResponseInterestDto findAll(UUID userId,
        CursorPageRequestInterestDto cursorPageRequestInterestDto) {

        getUserOrThrow(userId);

        Slice<Interest> slices = interestRepository.findAll(
            cursorPageRequestInterestDto.keyword(), cursorPageRequestInterestDto.orderBy(),
            cursorPageRequestInterestDto.direction(), cursorPageRequestInterestDto.cursor(),
            cursorPageRequestInterestDto.after(), cursorPageRequestInterestDto.limit());

        List<Interest> interests = slices.getContent();

        // Entity -> DTO
        Set<UUID> interestIds = interests.stream()
            .map(Interest::getId)
            .collect(Collectors.toSet());
        Set<UUID> subscribedIds = subscriptionRepository
            .findSubscribedInterestIds(userId, interestIds);    // 구독 여부 일괄 조회(bulk)
        List<InterestDto> interestDtos = new ArrayList<>();
        for (Interest interest : interests) {
            List<String> keywords = new ArrayList<>();
            for (InterestKeyword ik : interest.getKeywords()) {
                keywords.add(ik.getKeyword().getName());
            }

            boolean subscribedByMe = subscribedIds.contains(interest.getId());
            InterestDto dto = interestMapper.toDto(interest, keywords, subscribedByMe);
            interestDtos.add(dto);
        }

        long totalElements = interestRepository.countFilteredTotalElements(
            cursorPageRequestInterestDto.keyword(), cursorPageRequestInterestDto.orderBy(),
            cursorPageRequestInterestDto.direction());

        boolean hasNext = slices.hasNext();

        String nextCursor = null;
        Instant nextAfter = null;

        if (hasNext) {
            Interest lastInterest = slices.getContent().get(slices.getContent().size() - 1);

            if (cursorPageRequestInterestDto.orderBy() == InterestOrderBy.name) {
                nextCursor = lastInterest.getName();
            } else if (cursorPageRequestInterestDto.orderBy() == InterestOrderBy.subscriberCount) {
                nextCursor = String.valueOf(lastInterest.getSubscriberCount());
            }
            nextAfter = lastInterest.getCreatedAt();
        }

        return new CursorPageResponseInterestDto(interestDtos, nextCursor, nextAfter,
            slices.getSize(), totalElements, hasNext);
    }

    private User getUserOrThrow(UUID userId) {
        return userRepository.findById(userId).orElseThrow(
            () -> new UserNotFoundException(userId));
    }

    private Interest getByIdOrThrow(UUID id) {
        return interestRepository.findById(id).orElseThrow(
            () -> new InterestNotFoundException(id));
    }

    private Interest getByIdWithKeywordsOrThrow(UUID id) {
        return interestRepository.findByIdWithKeywords(id).orElseThrow(
            () -> new InterestNotFoundException(id));
    }

    private void updateKeywords(Interest interest, List<String> requestKeywords) {

        Map<String, InterestKeyword> savedKeywords = interest.getKeywords().stream()
            .collect(Collectors.toMap(ik -> ik.getKeyword().getName(), ik -> ik));

        Set<String> requestKeywordSet = new HashSet<>(requestKeywords);

        List<Keyword> existingKeywords = keywordRepository.findByNameIn(requestKeywordSet);
        Map<String, Keyword> existingKeywordMap = existingKeywords.stream()
            .collect(Collectors.toMap(Keyword::getName, k -> k));

        for (String keyword : requestKeywordSet) {
            if (!savedKeywords.containsKey(keyword)) {
                Keyword getKeyword = existingKeywordMap.getOrDefault(keyword, new Keyword(keyword));
                if (getKeyword.getId() == null) {
                    getKeyword = keywordRepository.save(getKeyword);
                }
                interest.addKeyword(getKeyword);
            } else {
                savedKeywords.remove(keyword);
            }
        }

        removeOrphanKeywords(interest, savedKeywords);
    }

    private void removeOrphanKeywords(Interest interest, Map<String, InterestKeyword> toRemove) {

        if (toRemove.isEmpty()) {
            return;
        }
        List<Keyword> removedKeyword = new ArrayList<>();

        for (InterestKeyword interestKeyword : toRemove.values()) {
            interest.getKeywords().remove(interestKeyword);
            removedKeyword.add(interestKeyword.getKeyword());
        }

        List<Keyword> toDelete = keywordRepository.findOrphanKeywordsIn(removedKeyword);
        keywordRepository.deleteAll(toDelete);

    }

}
