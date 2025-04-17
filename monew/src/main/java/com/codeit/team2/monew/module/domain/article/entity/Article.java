package com.codeit.team2.monew.module.domain.article.entity;


import com.codeit.team2.monew.module.domain.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.time.Instant;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@Entity
@Table(name = "articles")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Article extends BaseEntity {

    //TODO : interest 필드 추가
    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String source;

    @Column(nullable = false, unique = true)
    private String sourceUrl;

    @Column(nullable = false)
    private String summary;

    private Integer viewCount;
    private Instant publishedDate;
    private Boolean isDeleted;
}
