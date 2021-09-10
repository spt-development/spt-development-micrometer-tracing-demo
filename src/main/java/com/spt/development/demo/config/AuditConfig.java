package com.spt.development.demo.config;

import org.springframework.boot.actuate.audit.AuditEventRepository;
import org.springframework.boot.actuate.audit.InMemoryAuditEventRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AuditConfig {

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
}
