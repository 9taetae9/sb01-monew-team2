package com.codeit.team2.monew.module.domain.interest.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.codeit.team2.monew.module.domain.interest.exception.InvalidSimilarityInputException;
import org.apache.commons.text.similarity.LevenshteinDistance;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@Tag("integration")
@ActiveProfiles({"test-temp"})
class InterestNameSimilarityServiceTest {

    private static final double DEFAULT_THRESHOLD = 0.8;

    @Autowired
    private LevenshteinDistance levenshteinDistance;

    @Autowired
    private InterestNameSimilarityService interestNameSimilarityService;

    @Nested
    @DisplayName("calculateSimilarity 메서드 테스트")
    class CalculateSimilarityTests {

        @DisplayName("같은 단어를 비교하면 유사도는 1.0이다")
        @Test
        void sameWordsSimilarity() {
            String word1 = "사과";
            String word2 = "사과";

            double result = interestNameSimilarityService.calculateSimilarity(word1, word2);
            System.out.println(word1 + " 와 " + word2 + " : " + result);

            assertThat(result).isEqualTo(1.0);
        }

        @DisplayName("공백 기준으로 글자가 다를때 유사도가 낮은 경우를 확인한다.")
        @Test
        void wordsDifferByWhitespaceAndWord() {
            String word1 = "개발자 모임";
            String word2 = "개발자 공부";

            double result = interestNameSimilarityService.calculateSimilarity(word1, word2);
            System.out.println(word1 + " 와 " + word2 + " : " + result);

            assertThat(result).isBetween(0.0, DEFAULT_THRESHOLD);
        }

        @Test
        @DisplayName("띄어쓰기만 다른 문자열은 비슷하다고 판단한다")
        void wordsDifferOnlyByWhitespace() {
            String word1 = "핸드폰 케이스";
            String word2 = "핸드폰케이스";

            double result = interestNameSimilarityService.calculateSimilarity(word1, word2);
            System.out.println(word1 + " 와 " + word2 + " : " + result);

            assertThat(result).isBetween(DEFAULT_THRESHOLD, 1.0);
        }

        @DisplayName("완전히 다른 단어는 유사도가 낮다")
        @Test
        void completelyDifferentWords() {
            double result = interestNameSimilarityService.calculateSimilarity("사과", "자동차");

            assertThat(result).isLessThan(0.5);
        }

        @DisplayName("공백 혹은 빈 문자열이 포함된 경우 유사도는 0.0이다")
        @Test
        void emptyOrWhitespaceOnly() {
            double result = interestNameSimilarityService.calculateSimilarity("   ", "사과");

            assertThat(result).isEqualTo(0.0);
        }

        @DisplayName("입력값이 null이면 예외를 던진다")
        @Test
        void nullInputThrowsException() {
            assertThatThrownBy(() -> interestNameSimilarityService.calculateSimilarity(null, "사과"))
                .isInstanceOf(InvalidSimilarityInputException.class);
        }
    }

    @Nested
    @DisplayName("isSimilar 메서드 테스트")
    class IsSimilarTests {

        @DisplayName("두 글자에 한 글자만 추가된 글자는 유사하지 않다고 판단한다. (기준 0.8)")
        @Test
        void isSimilarAddWord() {
            String word1 = "사과";
            String word2 = "사과다";

            boolean result = interestNameSimilarityService.isSimilar(word1, word2,
                DEFAULT_THRESHOLD);
            System.out.println(word1 + " 와 " + word2 + " : " + result);

            assertThat(result).isFalse();
        }

        @DisplayName("입력값이 null이면 예외를 던진다")
        @Test
        void nullInputThrowsException() {
            assertThatThrownBy(
                () -> interestNameSimilarityService.isSimilar(null, "사과", DEFAULT_THRESHOLD))
                .isInstanceOf(InvalidSimilarityInputException.class);
        }

        @DisplayName("임계값이 0~1 범위를 벗어나면 예외를 던진다")
        @Test
        void invalidThresholdThrowsException() {
            assertThatThrownBy(() -> interestNameSimilarityService.isSimilar("사과", "사과", 1.5))
                .isInstanceOf(InvalidSimilarityInputException.class);
        }
    }
}
