package com.codeit.team2.monew.module.domain.subscription.entity;

import com.codeit.team2.monew.module.domain.BaseEntity;
import com.codeit.team2.monew.module.domain.interest.entity.Interest;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name="subscription")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Subscription extends BaseEntity {

//    @ManyToOne
//    @JoinColumn(name = "user_id")
//    private User user;

    @ManyToOne
    @JoinColumn(name = "interest_id")
    private Interest interest;

}
