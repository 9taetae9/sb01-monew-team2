package com.codeit.team2.monew.module.domain.article.mapper;


import com.codeit.team2.monew.module.domain.article.dto.ArticleViewDto;
import com.codeit.team2.monew.module.domain.article.dto.NaverArticleItemDto;
import com.codeit.team2.monew.module.domain.article.dto.rss.ChosunRss;
import com.codeit.team2.monew.module.domain.article.dto.rss.HankyungRss;
import com.codeit.team2.monew.module.domain.article.dto.rss.YonhapRss;
import com.codeit.team2.monew.module.domain.article.entity.Article;
import com.codeit.team2.monew.module.domain.article.entity.ArticleView;
import com.codeit.team2.monew.module.domain.article.entity.DummyArticle;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", imports = {ZonedDateTime.class, DateTimeFormatter.class})
public interface ArticleMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(source = "link", target = "sourceUrl")
    @Mapping(source = "description", target = "summary")
    @Mapping(target = "viewCount", constant = "0L")
    @Mapping(target = "publishedDate", expression = "java(ZonedDateTime.parse(dto.pubDate(), DateTimeFormatter.RFC_1123_DATE_TIME).toInstant())")
    @Mapping(target = "deleted", constant = "false")
    @Mapping(target = "source", constant = "NAVER")
    Article naverArticleItemToEntity(NaverArticleItemDto dto);


    @Mapping(target = "id", ignore = true)
    @Mapping(target = "title", source = "title")
    @Mapping(target = "source", constant = "HANKYUNG")
    @Mapping(target = "sourceUrl", source = "link")
    @Mapping(target = "viewCount", constant = "0L")
    @Mapping(target = "summary", constant = "")
    @Mapping(target = "publishedDate", expression = "java(ZonedDateTime.parse(item.getPubDate(), DateTimeFormatter.RFC_1123_DATE_TIME).toInstant())")
    @Mapping(target = "deleted", constant = "false")
    DummyArticle hankyungRssToEntity(HankyungRss.Item item);


    @Mapping(target = "id", ignore = true)
    @Mapping(target = "title", source = "title")
    @Mapping(target = "source", constant = "CHOSUN")
    @Mapping(target = "sourceUrl", source = "link")
    @Mapping(target = "viewCount", constant = "0L")
    @Mapping(target = "summary", constant = "")
    @Mapping(target = "publishedDate", expression = "java(ZonedDateTime.parse(item.getPubDate(), DateTimeFormatter.RFC_1123_DATE_TIME).toInstant())")
    @Mapping(target = "deleted", constant = "false")
    DummyArticle chosunRsstoEntity(ChosunRss.Item item);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "title", source = "title")
    @Mapping(target = "source", constant = "YONHAP")
    @Mapping(target = "sourceUrl", source = "link")
    @Mapping(target = "viewCount", constant = "0L")
    @Mapping(target = "summary", source = "description")
    @Mapping(target = "publishedDate", expression = "java(ZonedDateTime.parse(item.getPubDate(), DateTimeFormatter.RFC_1123_DATE_TIME).toInstant())")
    @Mapping(target = "deleted", constant = "false")
    DummyArticle yonhapRssToEntity(YonhapRss.Item item);

    List<Article> naverArticleListToEntity(List<NaverArticleItemDto> dtoList);

    List<DummyArticle> hankyungRssListToEntity(List<HankyungRss.Item> items);

    List<DummyArticle> chosunRssListToEntity(List<ChosunRss.Item> items);

    List<DummyArticle> yonhapRssListToEntity(List<YonhapRss.Item> items);

    @Mapping(target = "articleInterests", ignore = true)
    Article dummyArticleToArticle(DummyArticle dummyArticle);

    @Mapping(target = "id", source = "articleView.id")
    @Mapping(target = "viewedBy", source = "userId")
    @Mapping(target = "createdAt", source = "articleView.createdAt")
    @Mapping(target = "articleId", source = "article.id")
    @Mapping(target = "source", source = "article.source")
    @Mapping(target = "sourceUrl", source = "article.sourceUrl")
    @Mapping(target = "articleTitle", source = "article.title")
    @Mapping(target = "articlePublishedDate", source = "article.publishedDate")
    @Mapping(target = "articleSummary", source = "article.summary")
    @Mapping(target = "articleCommentCount", source = "commentCount")
    @Mapping(target = "articleViewCount", source = "article.viewCount")
    ArticleViewDto toResponseDto(Article article, ArticleView articleView, UUID userId,
        int commentCount);


}
