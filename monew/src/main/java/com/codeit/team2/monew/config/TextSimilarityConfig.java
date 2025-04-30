package com.codeit.team2.monew.config;

import org.apache.commons.text.similarity.LevenshteinDistance;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TextSimilarityConfig {

    @Bean
    public LevenshteinDistance levenshteinDistance() {
        return new LevenshteinDistance();
    }
}
