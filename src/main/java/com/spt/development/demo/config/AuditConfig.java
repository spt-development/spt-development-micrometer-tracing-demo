package com.spt.development.demo.config;

import com.spt.development.audit.spring.CorrelationIdProvider;
import io.micrometer.tracing.TraceContext;
import io.micrometer.tracing.Tracer;
import org.springframework.boot.actuate.audit.AuditEventRepository;
import org.springframework.boot.actuate.audit.InMemoryAuditEventRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Optional;

@Configuration
public class AuditConfig {

    @Bean
    public CorrelationIdProvider correlationIdProvider(Tracer tracer) {
        return new MicrometerCorrelationIdProvider(tracer);
    }

    /**
     * This bean is required to switch actuator security auditing on - see
     * {@link org.springframework.boot.actuate.autoconfigure.audit.AuditAutoConfiguration}.
     *
     * @return an instance of {@link InMemoryAuditEventRepository}.
     */
    @Bean
    public AuditEventRepository auditEventRepository() {
        return new InMemoryAuditEventRepository();
    }

    static class MicrometerCorrelationIdProvider implements CorrelationIdProvider {
        private final Tracer tracer;

        public MicrometerCorrelationIdProvider(Tracer tracer) {
            this.tracer = tracer;
        }

        @Override
        public String getCorrelationId() {
            return Optional.ofNullable(tracer.currentTraceContext().context()).map(TraceContext::traceId).orElse("no-trace-id");
        }
    }
}
