package com.codeit.team2.monew.module.domain.interest.entity;

import com.codeit.team2.monew.module.domain.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name="interest_keywords")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class InterestKeyword extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "interest_id")
    private Interest interest;

    @ManyToOne
    @JoinColumn(name = "keyword_id")
    private Keyword keyword;

}
