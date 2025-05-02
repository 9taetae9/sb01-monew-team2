package com.codeit.team2.monew.module.domain.article.service;


import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;

import com.codeit.team2.monew.module.TestEntityFactory;
import com.codeit.team2.monew.module.domain.article.entity.Article;
import com.codeit.team2.monew.module.domain.article.entity.DummyArticle;
import com.codeit.team2.monew.module.domain.article.external.RssNewsClient;
import com.codeit.team2.monew.module.domain.article.repository.ArticleRepository;
import com.codeit.team2.monew.module.domain.article.repository.DummyArticleRepository;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
public class RssFetchServiceTest {

    @Mock
    private RssNewsClient client;
    @Mock
    private JdbcTemplate jdbcTemplate;
    @Mock
    private DummyArticleRepository dummyArticleRepository;
    @Mock
    private ArticleRepository articleRepository;
    @Mock
    private List<RssNewsClient> clients;
    @Mock
    private DummyArticleService dummyArticleService;
    @InjectMocks
    private RssFetchService rssFetchService;


    @Test
    void fetchAllRss_should_call() {

        // given
        DummyArticle dummyArticle = TestEntityFactory.dummyArticle("test");
        Article article = TestEntityFactory.createArticle("test");
        ReflectionTestUtils.setField(rssFetchService, "clients", List.of(client));
        BDDMockito.given(client.fetchArticles())
            .willReturn(List.of(dummyArticle));
        BDDMockito.doNothing().when(dummyArticleService).removeDuplicateArticles();

        // when
        rssFetchService.fetchAllRss();

        // then
        BDDMockito.then(jdbcTemplate).should(times(1))
            .batchUpdate(anyString(), anyList(), anyInt(), any());
//        BDDMockito.then(dummyArticleRepository).should().deleteAllBySourceUrlIn(anyList());

    }
}
