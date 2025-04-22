package com.codeit.team2.monew.module.domain.interest.service;

import com.codeit.team2.monew.module.domain.interest.repository.InterestRepository;
import com.codeit.team2.monew.module.domain.interest.repository.KeywordRepository;
import com.codeit.team2.monew.module.domain.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@ActiveProfiles("test")
@SpringBootTest
@Transactional
public class InterestServiceIntegrationTest {

    @Autowired
    private InterestService interestService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private KeywordRepository keywordRepository;

    @Autowired
    private InterestRepository interestRepository;

    @DisplayName("정상적으로 관심사를 생성한다.")
    @Test
    void create() {
      // given

      // when

      // then
    }

}
