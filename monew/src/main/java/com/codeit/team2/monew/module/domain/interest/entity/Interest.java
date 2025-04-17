package com.codeit.team2.monew.module.domain.interest.entity;

import com.codeit.team2.monew.module.domain.BaseEntity;
import com.codeit.team2.monew.module.domain.subscription.entity.Subscription;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

@Entity
@Table(name="interests")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Interest extends BaseEntity {

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false)
    @ColumnDefault("0")
    private int subscriberCount;

    @OneToMany(mappedBy = "interest", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<InterestKeyword> keywords;

    @OneToMany(mappedBy = "interest", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Subscription> subscriptions;

    @Builder
    public Interest (String name, int subscriberCount, List<InterestKeyword> keywords,
        List<Subscription> subscriptions) {
        this.name = name;
        this.subscriberCount = subscriberCount;
        this.keywords = keywords;
        this.subscriptions = subscriptions;
    }
}
