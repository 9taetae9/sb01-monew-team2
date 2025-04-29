package com.codeit.team2.monew.module.domain.interest.service;

import lombok.RequiredArgsConstructor;
import org.apache.commons.text.similarity.LevenshteinDistance;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class InterestNameSimilarityService {

    private final LevenshteinDistance levenshteinDistance;

    // 공백 제거 후 레벤슈타인 거리를 기반으로 유사도를 계산 (시간복잡도: O(mxn))
    // 캐시를 쓴다던가, 인덱스를 사용하여 개선..할 수 있음.
    public double calculateSimilarity(String word1, String word2) {
        if (word1 == null || word2 == null) {
            // TODO: 커스텀 예외
            throw new IllegalArgumentException("입력 단어는 null이 될 수 없음.");
        }

        String processedWord1 = word1.replaceAll("\\s+", "");
        String processedWord2 = word2.replaceAll("\\s+", "");

        if (processedWord1.isEmpty() && processedWord2.isEmpty()) {
            return 1.0; // 둘 다 비었을 경우 -> 완전 일치
        }

        // 레벤슈타인 거리 계산 <- 같아지려면 얼마나 변경하냐
        int distance = levenshteinDistance.apply(processedWord1, processedWord2);

        // 최대 길이 계산
        int maxLength = Math.max(processedWord1.length(), processedWord2.length());

        return 1.0 - ((double) distance / maxLength);
    }

    // 임계값을 기준으로
    public boolean isSimilar(String word1, String word2, double threshold) {
        if (threshold < 0.0 || threshold > 1.0) {
            throw new IllegalArgumentException("임계값은 0.0에서 1.0 사이여야 함.");
        }
        return calculateSimilarity(word1, word2) >= threshold;
    }
}
