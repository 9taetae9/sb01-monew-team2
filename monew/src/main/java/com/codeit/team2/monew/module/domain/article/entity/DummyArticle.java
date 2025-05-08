package com.codeit.team2.monew.module.domain.article.entity;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "dummy_articles")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class DummyArticle {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(updatable = false, nullable = false)
    private UUID id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String source;

    @Column(nullable = false, unique = true)
    private String sourceUrl;

    private String summary;
    private Long viewCount;
    private Instant publishedDate;
    private Boolean deleted;

    @Column(name = "body_tsv", columnDefinition = "tsvector", insertable = false, updatable = false)
    private String bodyTsv;
}
