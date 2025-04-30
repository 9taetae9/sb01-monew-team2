package com.codeit.team2.monew.module.domain.interest.service;

import com.codeit.team2.monew.module.domain.interest.exception.InvalidSimilarityInputException;
import lombok.RequiredArgsConstructor;
import org.apache.commons.text.similarity.LevenshteinDistance;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class InterestNameSimilarityService {

    private final LevenshteinDistance levenshteinDistance;

    // 공백 제거 후 레벤슈타인 거리를 기반으로 유사도를 계산 (시간복잡도: O(mxn))
    public double calculateSimilarity(String word1, String word2) {
        if (word1 == null || word2 == null) {
            throw new InvalidSimilarityInputException("입력 단어는 null이 될 수 없습니다.");
        }

        String processedWord1 = word1.toLowerCase().replaceAll("\\s+", "");
        String processedWord2 = word2.toLowerCase().replaceAll("\\s+", "");

        if (processedWord1.isEmpty() || processedWord2.isEmpty()) {
            return 0.0;
        }

        // 레벤슈타인 거리 계산 <- 같아지려면 얼마나 변경이 필요한지
        int distance = levenshteinDistance.apply(processedWord1, processedWord2);

        int maxLength = Math.max(processedWord1.length(), processedWord2.length());

        return 1.0 - ((double) distance / maxLength);
    }

    public boolean isSimilar(String word1, String word2, double threshold) {
        if (threshold < 0.0 || threshold > 1.0) {
            throw new InvalidSimilarityInputException(
                "임계값은 0.0에서 1.0 사이여야 합니다. threshold: " + threshold);
        }
        return calculateSimilarity(word1, word2) >= threshold;
    }
}
