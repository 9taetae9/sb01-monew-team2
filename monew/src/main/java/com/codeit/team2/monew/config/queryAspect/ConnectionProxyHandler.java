package com.codeit.team2.monew.config.queryAspect;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;

@Profile("dev")
@RequiredArgsConstructor
public class ConnectionProxyHandler implements InvocationHandler {

    private final Object connection;
    private final QueryStatistics queryStatistics;

    @Override
    public Object invoke(final Object proxy, final Method method, final Object[] args)
        throws Throwable {
        Object invokeResult = method.invoke(connection, args);
        if (isGeneratePrepareStatement(method)) {
            return Proxy.newProxyInstance(
                invokeResult.getClass().getClassLoader(),
                invokeResult.getClass().getInterfaces(),
                new PreparedStatementProxyHandler(invokeResult, queryStatistics)
            );
        }
        return invokeResult;
    }

    private boolean isGeneratePrepareStatement(final Method method) {
        return method.getName().contains("prepareStatement");
    }
}
