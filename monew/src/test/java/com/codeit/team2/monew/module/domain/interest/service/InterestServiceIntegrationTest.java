package com.codeit.team2.monew.module.domain.interest.service;

import com.codeit.team2.monew.module.domain.interest.repository.InterestRepository;
import com.codeit.team2.monew.module.domain.interest.repository.KeywordRepository;
import com.codeit.team2.monew.module.domain.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@ActiveProfiles("test")
@SpringBootTest
@Transactional
@Tag("integration")
public class InterestServiceIntegrationTest {

    @Autowired
    private InterestServiceImpl interestService;

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

    @DisplayName("관심사 관리에 키워드 수정이 정상적으로 수행된다.")
    @Test
    void update_success() {
      // given

      // when

      // then
    }

}
