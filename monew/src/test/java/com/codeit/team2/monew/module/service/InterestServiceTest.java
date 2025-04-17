package com.codeit.team2.monew.module.service;

import static org.junit.jupiter.api.Assertions.*;

import com.codeit.team2.monew.module.controller.interest.InterestRegisterRequest;
import com.codeit.team2.monew.module.domain.interest.dto.InterestDto;
import com.codeit.team2.monew.module.repository.interest.InterestRepository;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
class InterestServiceTest {

    @Mock
    private InterestRepository interestRepository;

    @InjectMocks
    private InterestService interestService;

    @DisplayName("관심사 정보를 받아 생성한다.")
    @Test
    void create() {
        // given
        List<String> keywords = List.of("이거");
        InterestRegisterRequest request =  new InterestRegisterRequest("이름", keywords);

        // when
        InterestDto interestDto = interestService.create(request);

        // then
        assertEquals(interestDto.name(), "이름");
        assertEquals(interestDto.keywords().get(0), "이거");

    }

}
