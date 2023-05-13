package com.spt.development.demo.jms;

import io.micrometer.tracing.CurrentTraceContext;
import io.micrometer.tracing.Tracer;
import lombok.AllArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Aspect
@Component
@AllArgsConstructor
@Order(Ordered.HIGHEST_PRECEDENCE)
public class JmsTracePropagator {
    private static final int CORRELATION_ID_ARG_INDEX = 0;

    private final Tracer tracer;

    // This is required until this ticket is actioned: https://github.com/spring-projects/spring-framework/issues/30335
    @Around("@annotation(org.springframework.jms.annotation.JmsListener)")
    public Object propagate(ProceedingJoinPoint joinPoint) throws Throwable {
        final CurrentTraceContext.Scope scope = tracer.currentTraceContext().newScope(
            tracer.traceContextBuilder()
                  .traceId(joinPoint.getArgs()[CORRELATION_ID_ARG_INDEX].toString())
                  .spanId(tracer.nextSpan().context().spanId())
                  .build()
        );

        try (scope) {
            return joinPoint.proceed();
        }
    }
}
