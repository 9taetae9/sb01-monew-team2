package com.codeit.team2.monew.module.domain.article.backup.batch;

import static org.assertj.core.api.Assertions.assertThat;

import com.codeit.team2.monew.module.domain.article.backup.dto.ArticleBackupDto;
import com.codeit.team2.monew.module.domain.article.entity.Article;
import com.codeit.team2.monew.module.domain.interest.entity.Interest;
import com.codeit.team2.monew.module.domain.relation.entity.ArticleInterest;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class ArticleBackupProcessorTest {

    private ArticleBackupProcessor processor;

    @BeforeEach
    void setUp() {
        processor = new ArticleBackupProcessor();
    }

    @Test
    @DisplayName("기사(관심사 존재)를 백업 DTO로 변환")
    void process_ShouldTransformArticleToBackupDto() throws Exception {
        UUID articleId = UUID.randomUUID();
        UUID interestId1 = UUID.randomUUID();
        UUID interestId2 = UUID.randomUUID();

        Interest interest1 = Interest.create("관심사1");
        ReflectionTestUtils.setField(interest1, "id", interestId1);

        Interest interest2 = Interest.create("관심사2");
        ReflectionTestUtils.setField(interest2, "id", interestId2);

        ArticleInterest articleInterest1 = new ArticleInterest();
        ReflectionTestUtils.setField(articleInterest1, "interest", interest1);

        ArticleInterest articleInterest2 = new ArticleInterest();
        ReflectionTestUtils.setField(articleInterest2, "interest", interest2);

        Set<ArticleInterest> articleInterests = new HashSet<>();
        articleInterests.add(articleInterest1);
        articleInterests.add(articleInterest2);

        Instant publishedDate = Instant.now();
        Article article = new Article(
            "테스트 제목",
            "테스트 출처",
            "http://test.com",
            "테스트 요약",
            articleInterests,
            10L,
            publishedDate,
            false,
            null
        );
        ReflectionTestUtils.setField(article, "id", articleId);

        ArticleBackupDto result = processor.process(article);

        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(articleId);
        assertThat(result.title()).isEqualTo("테스트 제목");
        assertThat(result.source()).isEqualTo("테스트 출처");
        assertThat(result.sourceUrl()).isEqualTo("http://test.com");
        assertThat(result.summary()).isEqualTo("테스트 요약");
        assertThat(result.viewCount()).isEqualTo(10L);
        assertThat(result.publishedDate()).isEqualTo(publishedDate);
        assertThat(result.deleted()).isFalse();
        assertThat(result.interestIds()).hasSize(2);
        assertThat(result.interestIds()).contains(interestId1, interestId2);
        assertThat(result.backupDate()).isNotNull();
    }

    @Test
    @DisplayName("기사(관심사 존재 x) 백업 DTO로 변환")
    void process_ShouldHandleArticleWithoutInterests() throws Exception {
        UUID articleId = UUID.randomUUID();
        Article article = new Article(
            "테스트 제목",
            "테스트 출처",
            "http://test.com",
            "테스트 요약",
            new HashSet<>(),
            5L,
            Instant.now(),
            false,
            null
        );
        ReflectionTestUtils.setField(article, "id", articleId);

        ArticleBackupDto result = processor.process(article);

        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(articleId);
        assertThat(result.interestIds()).isEmpty();
    }
}
