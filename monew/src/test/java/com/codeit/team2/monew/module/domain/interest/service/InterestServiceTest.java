package com.codeit.team2.monew.module.domain.interest.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.codeit.team2.monew.module.domain.interest.dto.request.InterestRegisterRequest;
import com.codeit.team2.monew.module.domain.interest.dto.request.InterestUpdateRequest;
import com.codeit.team2.monew.module.domain.interest.dto.response.InterestDto;
import com.codeit.team2.monew.module.domain.interest.entity.Interest;
import com.codeit.team2.monew.module.domain.interest.entity.Keyword;
import com.codeit.team2.monew.module.domain.interest.repository.InterestRepository;
import com.codeit.team2.monew.module.domain.interest.repository.KeywordRepository;
import com.codeit.team2.monew.module.domain.subscription.repository.SubscriptionRepository;
import com.codeit.team2.monew.module.domain.user.entity.User;
import com.codeit.team2.monew.module.domain.user.repository.UserRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class InterestServiceTest {

    @Mock
    private InterestRepository interestRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private KeywordRepository keywordRepository;

    @Mock
    private SubscriptionRepository subscriptionRepository;

    @InjectMocks
    private InterestService interestService;

    @DisplayName("관심사를 생성하고 정상 응답한다.")
    @Test
    void create_success() {
        // given
        UUID userId = UUID.randomUUID();
        User user = mock(User.class);

        String name = "채소";
        List<String> inputKeywords = List.of("당근", "시금치");
        InterestRegisterRequest request = new InterestRegisterRequest(name, inputKeywords);

        Interest mockInterest = createInterest(name, inputKeywords);

        // mocking
        // user 를 찾았다고 가정
        when(userRepository.findById(any(UUID.class)))
            .thenReturn(Optional.of(user));

        // 해당 키워드가 DB에 없다고 가정
        when(keywordRepository.findByName(any(String.class)))
            .thenReturn(Optional.empty());

        when(keywordRepository.save(any(Keyword.class)))
            .thenAnswer(invocation -> {
                return invocation.getArgument(0);
            });

        // 생성
        when(interestRepository.save(any(Interest.class)))
            .thenReturn(mockInterest);

        // when
        InterestDto result = interestService.create(request, userId);

        // then
        assertThat(result.name()).isEqualTo(name);
        assertThat(result.keywords()).hasSize(2)
            .contains("당근", "시금치");
        assertThat(result.subscriberCount()).isEqualTo(0);
        assertThat(result.subscribedByMe()).isEqualTo(false);
    }

    // TODO : CREATE 유사도 80% 이상으로 생성에 실패한 경우

    @DisplayName("관심사 수정에서 키워드 추가가 정상적으로 수정된다.")
    @Test
    void update_success() {
      // given
        UUID userId = UUID.randomUUID();
        User user = mock(User.class);

        UUID interestId = UUID.randomUUID();

        String name = "채소";
        List<String> keywords = List.of("당근", "시금치");
        Interest mockInterest = createInterest(name, keywords);

        List<String> inputKeywords = List.of("당근", "시금치", "파");
        InterestUpdateRequest request = new InterestUpdateRequest(inputKeywords);

        when(interestRepository.findById(any(UUID.class)))
            .thenReturn(Optional.of(mockInterest));

      // when
        InterestDto result = interestService.update(request, interestId ,userId);

      // then
        assertThat(result.keywords()).hasSize(3).contains("파");
    }


    Interest createInterest(String name, List<String> keywords) {
        Interest mockInterest = Interest.create(name);
        for (String keyword : keywords) {
            mockInterest.addInterestKeyword(new Keyword(keyword));
        }
        return mockInterest;
    }

}
