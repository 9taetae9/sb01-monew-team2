package com.codeit.team2.monew.module.domain.interest.mapper;

import com.codeit.team2.monew.module.domain.interest.dto.response.InterestDto;
import com.codeit.team2.monew.module.domain.interest.entity.Interest;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface InterestMapper {

    @Mapping(source = "interest.name", target = "name")
    @Mapping(source = "keywords", target = "keywords")
    @Mapping(source = "subscribedByMe", target = "subscribedByMe")
    InterestDto toDto(Interest interest, List<String> keywords, Boolean subscribedByMe);

}
