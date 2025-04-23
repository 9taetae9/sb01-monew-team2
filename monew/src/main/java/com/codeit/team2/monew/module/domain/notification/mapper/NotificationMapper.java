package com.codeit.team2.monew.module.domain.notification.mapper;

import com.codeit.team2.monew.module.domain.notification.dto.NotificationDto;
import com.codeit.team2.monew.module.domain.notification.entity.Notification;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface NotificationMapper {

    @Mapping(source = "user.id", target = "userId")
    NotificationDto toDto(Notification notification);
}
