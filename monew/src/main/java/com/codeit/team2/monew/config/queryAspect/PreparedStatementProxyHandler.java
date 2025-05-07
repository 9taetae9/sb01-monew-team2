package com.codeit.team2.monew.config.queryAspect;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class PreparedStatementProxyHandler implements InvocationHandler {

    private final Object preparedStatement;
    private final QueryStatistics queryStatistics;

    @Override
    public Object invoke(final Object proxy, final Method method, final Object[] args)
        throws Throwable {
        if (isExecuteQuery(method)) {
            final Long beforeTime = System.currentTimeMillis();
            final Object result = method.invoke(preparedStatement, args);
            final Long afterTime = System.currentTimeMillis();
            queryStatistics.addQueryCount();
            queryStatistics.addQueryTime(afterTime - beforeTime);

            return result;
        }
        return method.invoke(preparedStatement, args);
    }

    /**
     * @param method
     * @return 이 메소드가 execute, executeUpdate 등 쿼리를 실행하는 메소드인지 확인함
     */
    private boolean isExecuteQuery(final Method method) {
        return method.getName().contains("execute");
    }
}
