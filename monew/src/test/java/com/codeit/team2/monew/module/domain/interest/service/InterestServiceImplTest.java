package com.codeit.team2.monew.module.domain.interest.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.codeit.team2.monew.module.domain.interest.TestInterestFactory;
import com.codeit.team2.monew.module.domain.interest.dto.request.CursorPageRequestInterestDto;
import com.codeit.team2.monew.module.domain.interest.dto.request.InterestOrderBy;
import com.codeit.team2.monew.module.domain.interest.dto.request.InterestRegisterRequest;
import com.codeit.team2.monew.module.domain.interest.dto.request.InterestUpdateRequest;
import com.codeit.team2.monew.module.domain.interest.dto.response.CursorPageResponseInterestDto;
import com.codeit.team2.monew.module.domain.interest.dto.response.InterestDto;
import com.codeit.team2.monew.module.domain.interest.entity.Interest;
import com.codeit.team2.monew.module.domain.interest.entity.Keyword;
import com.codeit.team2.monew.module.domain.interest.mapper.InterestMapper;
import com.codeit.team2.monew.module.domain.interest.repository.InterestCustomRepository;
import com.codeit.team2.monew.module.domain.interest.repository.InterestKeywordRepository;
import com.codeit.team2.monew.module.domain.interest.repository.InterestRepository;
import com.codeit.team2.monew.module.domain.interest.repository.KeywordRepository;
import com.codeit.team2.monew.module.domain.subscription.repository.SubscriptionRepository;
import com.codeit.team2.monew.module.domain.user.TestUserFactory;
import com.codeit.team2.monew.module.domain.user.entity.User;
import com.codeit.team2.monew.module.domain.user.repository.UserRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.data.domain.Sort.Direction;

@ExtendWith(MockitoExtension.class)
class InterestServiceImplTest {

    @Mock
    private InterestRepository interestRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private KeywordRepository keywordRepository;

    @Mock
    private InterestKeywordRepository interestKeywordRepository;

    @Mock
    private SubscriptionRepository subscriptionRepository;
    @Mock
    private InterestCustomRepository interestCustomRepository;

    @Spy
    private InterestMapper interestMapper = Mappers.getMapper(InterestMapper.class);

    @InjectMocks
    private InterestServiceImpl interestService;

    @DisplayName("관심사를 생성하고 정상 응답한다.")
    @Test
    void create_success() {
        // given
        User user = TestUserFactory.createWithName("name");

        String name = "채소";
        List<String> inputKeywords = List.of("당근", "시금치");
        InterestRegisterRequest request = new InterestRegisterRequest(name, inputKeywords);
        Interest mockInterest = TestInterestFactory.create(name, inputKeywords);

        when(userRepository.findById(any(UUID.class)))
            .thenReturn(Optional.of(user));
        when(keywordRepository.findByName(any(String.class)))
            .thenReturn(Optional.empty());
        when(interestRepository.existsByNameSimilarTo(any(String.class)))
            .thenReturn(false);
        when(keywordRepository.save(any(Keyword.class)))
            .thenAnswer(invocation -> {
                return invocation.getArgument(0);
            });
        when(interestRepository.save(any(Interest.class)))
            .thenReturn(mockInterest);

        // when
        InterestDto result = interestService.create(request, user.getId());

        // then
        assertThat(result.name()).isEqualTo(name);
        assertThat(result.keywords()).hasSize(2)
            .contains("당근", "시금치");
        assertThat(result.subscriberCount()).isEqualTo(0);
        assertThat(result.subscribedByMe()).isEqualTo(false);
    }

    @DisplayName("비슷한 관심사명이 있는 경우 관심사 생성에 실패한다.")
    @Test
    void create_failure() {
        // given
        User user = TestUserFactory.createWithName("name");

        String name = "채소";
        List<String> inputKeywords = List.of("당근", "시금치");
        InterestRegisterRequest request = new InterestRegisterRequest(name, inputKeywords);

        when(userRepository.findById(any(UUID.class)))
            .thenReturn(Optional.of(user));
        when(interestRepository.existsByNameSimilarTo(any(String.class)))
            .thenReturn(true);

        // when & then
        assertThatThrownBy(() -> interestService.create(request, user.getId()))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("관심사 수정에서 키워드 추가/삭제가 정상적으로 수행된다.")
    @Test
    void update_success() {
        // given
        User user = TestUserFactory.createWithName("name");

        String name = "채소";
        List<String> keywords = List.of("당근");
        Interest interest = TestInterestFactory.create(name, keywords);

        List<String> inputKeywords = List.of("시금치");
        InterestUpdateRequest request = new InterestUpdateRequest(inputKeywords);

        when(userRepository.findById(any(UUID.class)))
            .thenReturn(Optional.of(user));
        when(interestRepository.findById(any(UUID.class)))
            .thenReturn(Optional.of(interest));
        when(keywordRepository.findByName(any(String.class)))
            .thenReturn(Optional.empty());
        when(keywordRepository.save(any(Keyword.class)))
            .thenAnswer(invocation -> {
                return invocation.getArgument(0);
            });
        when(interestKeywordRepository.existsByKeyword(any(Keyword.class)))
            .thenReturn(false);

        // when
        InterestDto result = interestService.update(request, interest.getId(), user.getId());

        // then
        assertThat(result.keywords()).hasSize(1)
            .contains("시금치").doesNotContain("당근");
        verify(keywordRepository).delete(any(Keyword.class));
        verify(keywordRepository).save(any(Keyword.class));
    }

    Interest createInterest(String name, List<String> keywords) {
        Interest mockInterest = Interest.create(name);
        for (String keyword : keywords) {
            mockInterest.addKeyword(new Keyword(keyword));
        }
        return mockInterest;
    }

    @DisplayName("관심사 삭제가 수행된다.")
    @Test
    void delete() {
        // given
        User user = TestUserFactory.createWithName("name");

        String name = "채소";
        List<String> keywords = List.of("당근");
        Interest interest = TestInterestFactory.create(name, keywords);

        when(userRepository.findById(any(UUID.class)))
            .thenReturn(Optional.of(user));
        when(interestRepository.findById(any(UUID.class)))
            .thenReturn(Optional.of(interest));

        // when
        interestService.delete(interest.getId(), user.getId());

        // then
        verify(interestRepository).delete(any(Interest.class));
        verify(keywordRepository).deleteAllOrphanKeywords();
    }

    @DisplayName("관심사 목록을 조회한다.")
    @Test
    void findAll() {
        // given
        User user = TestUserFactory.createWithName("name");
        UUID userId = user.getId();
        when(userRepository.existsById(userId)).thenReturn(true);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        CursorPageRequestInterestDto cursorPageRequestInterestDto = new CursorPageRequestInterestDto(
            null,
            InterestOrderBy.name, Direction.DESC, null, null, 3);

        Interest interest = TestInterestFactory.create("interest1", List.of("k1", "k2"));
        Slice<Interest> slices = new SliceImpl<>(List.of(interest), PageRequest.of(0, 3), false);
        when(interestCustomRepository.findAll(cursorPageRequestInterestDto.keyword(),
            cursorPageRequestInterestDto.orderBy(), cursorPageRequestInterestDto.direction(),
            cursorPageRequestInterestDto.cursor(), cursorPageRequestInterestDto.after(),
            cursorPageRequestInterestDto.limit())).thenReturn(slices);

        when(interestCustomRepository.countFilteredTotalElements(any(), any(), any())).thenReturn(
            1L);

        when(subscriptionRepository.existsByInterestAndUser(interest, user)).thenReturn(false);

        //when
        CursorPageResponseInterestDto result = interestService.findAll(userId,
            cursorPageRequestInterestDto);

        // then
        assertThat(result.content()).hasSize(1);
        assertThat(result.totalElements()).isEqualTo(1L);
        assertThat(result.hasNext()).isEqualTo(false);
        assertThat(result.nextCursor()).isNull();
        assertThat(result.nextAfter()).isNull();
        verify(userRepository).existsById(userId);
        verify(interestCustomRepository).findAll(cursorPageRequestInterestDto.keyword(),
            cursorPageRequestInterestDto.orderBy(), cursorPageRequestInterestDto.direction(),
            cursorPageRequestInterestDto.cursor(), cursorPageRequestInterestDto.after(),
            cursorPageRequestInterestDto.limit());

    }
}
