package com.codeit.team2.monew.module.mapper.interest;

import com.codeit.team2.monew.module.domain.interest.dto.InterestDto;
import com.codeit.team2.monew.module.domain.interest.entity.Interest;
import com.codeit.team2.monew.module.domain.interest.entity.Keyword;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface InterestMapper {

    InterestMapper INSTANCE = Mappers.getMapper(InterestMapper.class);

    @Mapping(source = "keywords", target = "keywords")
    InterestDto toDto(Interest interest, List<String> keywords, Boolean subscribedByMe);

}
