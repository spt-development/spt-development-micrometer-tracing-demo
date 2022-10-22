package com.spt.development.demo.auditing;

import com.spt.development.audit.spring.AuditEventWriter;
import com.spt.development.cid.CorrelationId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.actuate.audit.AuditEvent;
import org.springframework.boot.info.BuildProperties;

import static com.spt.development.test.LogbackUtil.verifyErrorLogging;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.boot.actuate.security.AuthenticationAuditListener.AUTHENTICATION_SUCCESS;

class AuditEventListenerTest {
    private interface TestData {
        String APP_NAME = "Test app name";
        String CORRELATION_ID = "443debaf-1a0b-4d60-8118-e458b75e24ff";
    }

    @BeforeEach
    void setUp() {
        CorrelationId.set(TestData.CORRELATION_ID);
    }

    @Test
    void onAuditEvent_errors_shouldLogException() {
        final AuditEventListenerArgs args = new AuditEventListenerArgs();

        doThrow(new RuntimeException("Test audit event listener failure")).when(args.auditEventWriter).write(any());

        final AuditEvent auditEvent = Mockito.mock(AuditEvent.class);
        when(auditEvent.getType()).thenReturn(AUTHENTICATION_SUCCESS);

        verifyErrorLogging(
                AuditEventListener.class,
                () -> {
                    createListener(args).onAuditEvent(auditEvent);
                    return null;
                },
                "Failed to send audit event"
        );
    }

    private AuditEventListener createListener(AuditEventListenerArgs args) {
        return new AuditEventListener(args.appName, args.buildProperties, args.auditEventWriter);
    }

    private static class AuditEventListenerArgs {
        String appName = TestData.APP_NAME;
        BuildProperties buildProperties = Mockito.mock(BuildProperties.class);
        AuditEventWriter auditEventWriter = Mockito.mock(AuditEventWriter.class);
    }
}