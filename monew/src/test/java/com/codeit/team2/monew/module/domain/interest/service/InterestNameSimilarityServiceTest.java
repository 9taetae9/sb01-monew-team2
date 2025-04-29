package com.codeit.team2.monew.module.domain.interest.service;

import static org.assertj.core.api.Assertions.assertThat;

import org.apache.commons.text.similarity.LevenshteinDistance;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Tag("integration")
class InterestNameSimilarityServiceTest {

    @Autowired
    private LevenshteinDistance levenshteinDistance;

    @Autowired
    private InterestNameSimilarityService interestNameSimilarityService;

    @DisplayName("두글자에 한글자만 추가된 글자와 비교할 경우 유사하지 않다고 판단한다. (0.8 기준)")
    @Test
    void isSimilar() {
        // given
        String word1 = "사과";
        String word2 = "사과다";

        // when
        boolean result = interestNameSimilarityService.isSimilar(word1, word2, 0.8);
        System.out.println(word1 + " 와 " + word2 + " : " + result);

        // then
        assertThat(result).isEqualTo(false);
    }


    @DisplayName("같은 단어를 비교하면 유사도는 1.0이다")
    @Test
    void sameWordsSimilarity() {
        // given
        String word1 = "사과";
        String word2 = "사과";

        // when
        double result = interestNameSimilarityService.calculateSimilarity(word1, word2);
        System.out.println(word1 + " 와 " + word2 + " : " + result);

        // then
        assertThat(result).isBetween(0.8, 1.0);
    }

    @DisplayName("띄어쓰기만 다른 문자열 유사도 비교 시 비슷하다고 판단한다.")
    @Test
    void wordsDifferOnlyByWhitespace() {
        // given
        String word1 = "핸드폰 케이스";
        String word2 = "핸드폰케이스";

        // when
        double result = interestNameSimilarityService.calculateSimilarity(word1, word2);
        System.out.println(word1 + " 와 " + word2 + " : " + result);

        // then
        assertThat(result).isBetween(0.8, 1.0);
    }
}
