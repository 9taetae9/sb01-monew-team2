package com.codeit.team2.monew.config.queryAspect;

import lombok.Getter;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

/**
 * API별로 쿼리 실행 수, 쿼리 총 실행 시간을 저장
 */
@Component
@RequestScope
@Getter
@Profile("dev")
public class QueryStatistics {

    private String apiUrl;
    private Long queryCounts = 0L;
    private Long queryTime = 0L;

    public void setApiUrl(String url) {
        this.apiUrl = url;
    }

    public void addQueryTime(Long queryTime) {
        this.queryTime += queryTime;
    }

    public void addQueryCount() {
        this.queryCounts++;
    }

}
