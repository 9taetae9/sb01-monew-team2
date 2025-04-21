package com.codeit.team2.monew.module.domain.interest.entity;

import com.codeit.team2.monew.module.domain.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Entity
@Table(name = "keywords")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Keyword extends BaseEntity {

    @Column(nullable = false, unique = true)
    private String name;

}
