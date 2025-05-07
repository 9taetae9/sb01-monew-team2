package com.codeit.team2.monew.config.queryAspect;

import java.lang.reflect.Proxy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Aspect
@Component
@Profile("dev")
@Slf4j
@RequiredArgsConstructor
public class QueryStatisticsAop {

    private final QueryStatistics queryStatistics;

    @Around("execution(* javax.sql.DataSource.getConnection())")
    public Object getConnection(ProceedingJoinPoint joinPoint) throws Throwable {
        Object connection = joinPoint.proceed();
        return Proxy.newProxyInstance(
            connection.getClass().getClassLoader(),
            connection.getClass().getInterfaces(),
            new ConnectionProxyHandler(connection, queryStatistics)
        );
    }

    @Around("within(@org.springframework.web.bind.annotation.RestController *)")
    public Object calculateExecutionTime(final ProceedingJoinPoint joinPoint) throws Throwable {
        ServletRequestAttributes attributes =
            (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            queryStatistics.setApiUrl(
                attributes.getRequest().getMethod() + " " + attributes.getRequest()
                    .getRequestURI());
        }

        Object result = joinPoint.proceed();

        log.info("Query Statistics: URL = {},  Query Count = {}, Query Time = {}(ms)",
            queryStatistics.getApiUrl(), queryStatistics.getQueryCounts(),
            queryStatistics.getQueryTime());
        return result;
    }
}
