package com.codeit.team2.monew.module.domain.interest.entity;

import com.codeit.team2.monew.module.domain.BaseEntity;
import com.codeit.team2.monew.module.domain.relation.entity.ArticleInterest;
import com.codeit.team2.monew.module.domain.subscription.entity.Subscription;
import com.codeit.team2.monew.module.domain.user.entity.User;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.HashSet;
import java.util.Set;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

@Entity
@Table(name = "interests")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Interest extends BaseEntity {

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false)
    @ColumnDefault("0L")
    private long subscriberCount;

    @OneToMany(mappedBy = "interest", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<InterestKeyword> keywords = new HashSet<>();

    @OneToMany(mappedBy = "interest", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Subscription> subscriptions = new HashSet<>();

    @OneToMany(mappedBy = "interest", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ArticleInterest> articleInterests = new HashSet<>();

    private Interest(String name, long subscriberCount) {
        this.name = name;
        this.subscriberCount = subscriberCount;
    }

    public static Interest create(String name) {
        return new Interest(name, 0L);
    }

    public InterestKeyword addInterestKeyword(Keyword keyword) {
        InterestKeyword interestKeyword = new InterestKeyword(this, keyword);
        this.keywords.add(interestKeyword);
        return interestKeyword;
    }

    public Subscription addSubscription(User user) {
        Subscription subscription = new Subscription(user, this);
        this.subscriptions.add(subscription);
        this.subscriberCount++;
        return subscription;
    }

}
