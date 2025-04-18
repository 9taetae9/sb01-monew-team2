package com.codeit.team2.monew.module.service;

import com.codeit.team2.monew.module.controller.interest.InterestRegisterRequest;
import com.codeit.team2.monew.module.domain.interest.dto.InterestDto;
import com.codeit.team2.monew.module.domain.interest.entity.Interest;
import com.codeit.team2.monew.module.domain.interest.entity.Keyword;
import com.codeit.team2.monew.module.mapper.interest.InterestMapper;
import com.codeit.team2.monew.module.repository.interest.InterestRepository;
import com.codeit.team2.monew.module.repository.interest.KeywordRepository;
import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class InterestService {

    private final InterestRepository interestRepository;
    private final KeywordRepository keywordRepository;

    @Transactional
    public InterestDto create(InterestRegisterRequest request) {

        // 관심사 이름 유사도 검사: 80% 이상 일치 시 등록 불가


        List<Keyword> newKeywords = new ArrayList<>();

        // 키워드 검색: keyword 테이블에 존재하는지
        // 이미 KEYWORD 테이블에 있다면 해당 키워드를 list 에 추가하기
        for (String keyword : request.keywords()) {
            Keyword getKeyword = keywordRepository.findByName(keyword)
                .orElse(new Keyword(keyword));
            newKeywords.add(getKeyword);
        }

        Interest interest = Interest.builder()
            .name(request.name())
//            .keywords(newKeywords)
            .subscriptions(new ArrayList<>())
            .subscriberCount(0)
            .build();

        return InterestMapper.INSTANCE.toDto(interest, List.of(request.keywords().get(0)) ,true);
    }

}
