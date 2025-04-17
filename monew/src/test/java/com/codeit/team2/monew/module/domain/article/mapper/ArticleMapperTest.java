package com.codeit.team2.monew.module.domain.article.mapper;


import com.codeit.team2.monew.module.domain.article.dto.NaverArticleItemDto;
import com.codeit.team2.monew.module.domain.article.entity.Article;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class ArticleMapperTest {

    private ArticleMapper mapper = Mappers.getMapper(ArticleMapper.class);

    @Test
    void testNaverArticleItemToEntity_success() {
        // given
        NaverArticleItemDto request = new NaverArticleItemDto(
            "test title",
            "https://testlink.com",
            "https://testlink.com",
            "test description",
            "Fri, 11 May 2018 09:50:00 +0900"
        );
        Instant time = ZonedDateTime.parse(request.pubDate(), DateTimeFormatter.RFC_1123_DATE_TIME)
            .toInstant();
        // when
        Article response = mapper.naverArticleItemToEntity(request);

        // then
        Assertions.assertThat(response.getTitle()).isEqualTo("test title");
        Assertions.assertThat(response.getPublishedDate()).isEqualTo(time);
    }

}
