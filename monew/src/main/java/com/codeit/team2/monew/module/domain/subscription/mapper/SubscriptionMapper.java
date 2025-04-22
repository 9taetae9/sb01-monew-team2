package com.codeit.team2.monew.module.domain.subscription.mapper;

import com.codeit.team2.monew.module.domain.interest.entity.Interest;
import com.codeit.team2.monew.module.domain.subscription.dto.SubscriptionDto;
import com.codeit.team2.monew.module.domain.subscription.entity.Subscription;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface SubscriptionMapper {

    SubscriptionMapper INSTANCE = Mappers.getMapper(SubscriptionMapper.class);

    @Mapping(source = "subscription.id", target = "id")
    @Mapping(source = "interest.id", target = "interestId")
    @Mapping(source = "interest.name", target = "interestName")
    @Mapping(source = "interestKeywords", target = "interestKeywords")
    @Mapping(source = "subscription.createdAt", target = "createdAt")
    SubscriptionDto toDto(Subscription subscription, Interest interest, List<String> interestKeywords);
}
