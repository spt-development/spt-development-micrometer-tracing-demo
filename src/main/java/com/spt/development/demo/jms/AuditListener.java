package com.spt.development.demo.jms;

import com.spt.development.audit.spring.AuditEvent;
import com.spt.development.demo.config.JmsConfig;
import com.spt.development.demo.repository.AuditRepository;
import lombok.AllArgsConstructor;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.support.JmsHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class AuditListener {
    private final AuditRepository auditRepository;

    @JmsListener(destination = JmsConfig.AUDIT_EVENT_QUEUE)
    public void onMessage(// NOTE. The correlation ID is used by spt-development-cid-jms-spring aspect
                          @Header(JmsHeaders.CORRELATION_ID) String correlationId,
                          @Payload String auditEventJson) {
        auditRepository.create(AuditEvent.fromJson(auditEventJson));
    }
}
