package com.codeit.team2.monew.module.domain.interest.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;

import com.codeit.team2.monew.module.domain.interest.controller.InterestRegisterRequest;
import com.codeit.team2.monew.module.domain.interest.dto.InterestDto;
import com.codeit.team2.monew.module.domain.interest.entity.Interest;
import com.codeit.team2.monew.module.domain.interest.entity.InterestKeyword;
import com.codeit.team2.monew.module.domain.interest.entity.Keyword;
import com.codeit.team2.monew.module.domain.interest.repository.InterestRepository;
import com.codeit.team2.monew.module.domain.interest.repository.KeywordRepository;
import com.codeit.team2.monew.module.domain.subscription.repository.SubscriptionRepository;
import com.codeit.team2.monew.module.domain.user.entity.User;
import com.codeit.team2.monew.module.domain.user.repository.UserRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

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

        String name = "채소";
        List<String> inputKeywords = List.of("당근", "시금치");
        InterestRegisterRequest request = new InterestRegisterRequest(name, inputKeywords);

        Interest mockInterest = Interest.create(name);
        Keyword keyword1 = new Keyword("당근");
        Keyword keyword2 = new Keyword("시금치");
        mockInterest.addInterestKeyword(keyword1);
        mockInterest.addInterestKeyword(keyword2);

        // mocking
        Mockito.when(userRepository.findById(any(UUID.class)))
            .thenReturn(Optional.empty());

        Mockito.when(keywordRepository.findByName(any(String.class)))
            .thenReturn(Optional.empty());

        Mockito.when(interestRepository.save(any(Interest.class)))
            .thenReturn(mockInterest);

        // when
        InterestDto result = interestService.create(request, userId.toString());

        // then
        assertEquals(name, result.name());
        assertEquals(result.keywords().size(), inputKeywords.size());
        assertEquals("당근", result.keywords().get(0));
        assertEquals(0, result.subscriberCount());
        assertEquals(false, result.subscribedByMe());

    }

}
