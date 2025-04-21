package com.codeit.team2.monew.module.domain.relation.entity;


import com.codeit.team2.monew.module.domain.BaseEntity;
import com.codeit.team2.monew.module.domain.article.entity.Article;
import com.codeit.team2.monew.module.domain.interest.entity.Interest;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "article_interests", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"article_id", "interest_id"})
})
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ArticleInterest extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "article_id", nullable = false)
    private Article article;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "interest_id", nullable = false)
    private Interest interest;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ArticleInterest)) {
            return false;
        }

        ArticleInterest that = (ArticleInterest) o;

        return Objects.equals(article != null ? article.getId() : null,
            that.article != null ? that.article.getId() : null)
            && Objects.equals(interest != null ? interest.getId() : null,
            that.interest != null ? that.interest.getId() : null);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
            article != null ? article.getId() : null,
            interest != null ? interest.getId() : null
        );
    }
}



