package com.codeit.team2.monew.module.domain.article.entity;


import com.codeit.team2.monew.module.domain.BaseEntity;
import com.codeit.team2.monew.module.domain.interest.entity.Interest;
import com.codeit.team2.monew.module.domain.relation.entity.ArticleInterest;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
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

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String source;

    @Column(nullable = false, unique = true)
    private String sourceUrl;

    @Column(nullable = false)
    private String summary;

    @OneToMany(mappedBy = "article", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ArticleInterest> articleInterests = new HashSet<>();

    //TODO
    public void addInterest(Interest interest, Set<String> attachedKeywords) {
        ArticleInterest articleInterest = new ArticleInterest();

    }


    private Integer viewCount;
    private Instant publishedDate;
    private Boolean isDeleted;
}
