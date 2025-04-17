package com.codeit.team2.monew.module.domain.article.mapper;


import com.codeit.team2.monew.module.domain.article.dto.NaverArticleItemDto;
import com.codeit.team2.monew.module.domain.article.entity.Article;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", imports = {ZonedDateTime.class, DateTimeFormatter.class})
public interface ArticleMapper {

    @Mapping(source = "originalLink", target = "sourceUrl")
    @Mapping(source = "description", target = "summary")
    @Mapping(target = "viewCount", constant = "0")
    @Mapping(target = "publishedDate", expression = "java(ZonedDateTime.parse(dto.pubDate(), DateTimeFormatter.RFC_1123_DATE_TIME).toInstant())")
    @Mapping(target = "isDeleted", constant = "false")
    Article naverArticleItemToEntity(NaverArticleItemDto dto);

    List<Article> naverArticleListToEntity(List<NaverArticleItemDto> dtoList);
}
