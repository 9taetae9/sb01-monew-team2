package com.codeit.team2.monew.module.domain.notification.entity;

import com.codeit.team2.monew.module.domain.BaseEntity;
import com.codeit.team2.monew.module.domain.user.entity.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Table(name = "notifications")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
public class Notification extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "user_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User user;

    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    private boolean confirmed;

    @Column(nullable = false)
    private UUID resourceId;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ResourceType resourceType;

    public Notification(User user, String content, UUID resourceId, ResourceType resourceType) {
        this.user = user;
        this.content = content;
        this.resourceId = resourceId;
        this.resourceType = resourceType;
        this.confirmed = false;
    }

    public void confirmNotification() {
        this.confirmed = true;
    }

}
