package com.codeit.team2.monew.module.domain.interest.Integration;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.codeit.team2.monew.module.domain.interest.dto.request.InterestRegisterRequest;
import com.codeit.team2.monew.module.domain.interest.entity.Interest;
import com.codeit.team2.monew.module.domain.interest.mapper.InterestMapper;
import com.codeit.team2.monew.module.domain.interest.repository.InterestCustomRepository;
import com.codeit.team2.monew.module.domain.interest.repository.InterestKeywordRepository;
import com.codeit.team2.monew.module.domain.interest.repository.InterestRepository;
import com.codeit.team2.monew.module.domain.interest.repository.KeywordRepository;
import com.codeit.team2.monew.module.domain.interest.service.InterestNameSimilarityService;
import com.codeit.team2.monew.module.domain.interest.service.InterestServiceImpl;
import com.codeit.team2.monew.module.domain.subscription.repository.SubscriptionRepository;
import com.codeit.team2.monew.module.domain.user.TestUserFactory;
import com.codeit.team2.monew.module.domain.user.entity.User;
import com.codeit.team2.monew.module.domain.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.apache.commons.text.similarity.LevenshteinDistance;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@Disabled
@SpringBootTest
@Transactional
@ActiveProfiles({"test-temp"})
@Tag("integration")
public class InterestNameSimilarityServiceTest {

    @MockitoBean
    private UserRepository userRepository;

    @MockitoBean
    private InterestCustomRepository interestCustomRepository;

    @Autowired
    private InterestRepository interestRepository;

    @Autowired
    private KeywordRepository keywordRepository;

    @Autowired
    private InterestKeywordRepository interestKeywordRepository;

    @Autowired
    private SubscriptionRepository subscriptionRepository;

    @Autowired
    private InterestNameSimilarityService interestNameSimilarityService;

    @Autowired
    private InterestMapper interestMapper;

    @Autowired
    private ApplicationEventPublisher publisher;

    @Autowired
    private InterestServiceImpl interestService;

    @Autowired
    private LevenshteinDistance levenshteinDistance;

    @DisplayName("관심사 이름 비교 후 생성 시간 확인")
    @Test
    void create_performanceTest() {
        // given
        int bulkSize = 500;
        for (int i = 0; i < bulkSize; i++) {
            Interest interest = Interest.create("테스트 관심사 " + i);
            interestRepository.save(interest);
        }
        interestRepository.flush();

        InterestRegisterRequest request = new InterestRegisterRequest(
            "안겹치게", // 일부러 유사하지 않게
            List.of("키워드1", "키워드2")
        );
        User user = TestUserFactory.createWithName("name");

        when(userRepository.findById(any(UUID.class)))
            .thenReturn(Optional.of(user));

        //when
        long startTime = System.currentTimeMillis();
        interestService.create(request, user.getId());
        long endTime = System.currentTimeMillis();

        System.out.println(
            "create() 호출 소요 시간: " + (endTime - startTime) + " ms"); // create() 호출 소요 시간: 978 ms
    }

}
