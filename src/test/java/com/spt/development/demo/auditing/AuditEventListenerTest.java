package com.spt.development.demo.auditing;

import com.spt.development.audit.spring.AuditEventWriter;
import io.micrometer.tracing.CurrentTraceContext;
import io.micrometer.tracing.TraceContext;
import io.micrometer.tracing.Tracer;
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
        String TRACE_ID = "443debaf-1a0b-4d60-8118-e458b75e24ff";
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
        return new AuditEventListener(args.appName, args.tracer, args.buildProperties, args.auditEventWriter);
    }

    private static class AuditEventListenerArgs {
        String appName = TestData.APP_NAME;
        Tracer tracer = Mockito.mock(Tracer.class);
        BuildProperties buildProperties = Mockito.mock(BuildProperties.class);
        AuditEventWriter auditEventWriter = Mockito.mock(AuditEventWriter.class);

        AuditEventListenerArgs() {
            final TraceContext context = Mockito.mock(TraceContext.class);
            when(context.traceId()).thenReturn(TestData.TRACE_ID);

            final CurrentTraceContext currentContext = Mockito.mock(CurrentTraceContext.class);
            when(currentContext.context()).thenReturn(context);

            when(tracer.currentTraceContext()).thenReturn(currentContext);
        }
    }
}