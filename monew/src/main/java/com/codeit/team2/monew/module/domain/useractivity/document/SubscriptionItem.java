package com.codeit.team2.monew.module.domain.useractivity.document;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "subscriptions")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
public class SubscriptionItem {

    private UUID id;
    private UUID interestId;
    private String interestName;
    private List<String> interestKeywords;
    private Long interestSubscriberCount;
    private Instant createdAt;

    public void updateInterestKeywords(List<String> keywords) {
        this.interestKeywords = keywords;
    }
}

