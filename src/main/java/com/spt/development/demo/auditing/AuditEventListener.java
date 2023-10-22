package com.spt.development.demo.auditing;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.spt.development.audit.spring.AuditEventWriter;
import io.micrometer.tracing.TraceContext;
import io.micrometer.tracing.Tracer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.audit.AuditEvent;
import org.springframework.boot.actuate.audit.listener.AbstractAuditListener;
import org.springframework.boot.info.BuildProperties;
import org.springframework.stereotype.Component;

import java.net.InetAddress;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.spt.development.audit.spring.util.HttpRequestUtils.getClientIpAddress;
import static com.spt.development.demo.util.Constants.Auditing;
import static org.springframework.boot.actuate.security.AuthenticationAuditListener.AUTHENTICATION_FAILURE;
import static org.springframework.boot.actuate.security.AuthenticationAuditListener.AUTHENTICATION_SUCCESS;

/**
 * This listener is an example of how the spt-development-audit-spring auditing framework can be combined with actuators
 * audit events to audit successful and failed login attempts.
 */
@Slf4j
@Component
public class AuditEventListener extends AbstractAuditListener {
    private static final Set<String> AUTHENTICATION_EVENTS =
            Stream.of(AUTHENTICATION_SUCCESS, AUTHENTICATION_FAILURE).collect(Collectors.toSet());

    private static final Gson GSON = new GsonBuilder().create();

    private final String appName;
    private final Tracer tracer;
    private final BuildProperties buildProperties;
    private final AuditEventWriter auditEventWriter;

    public AuditEventListener(
            @Value("${spring.application.name}")
            final String appName,
            final Tracer tracer,
            final BuildProperties buildProperties,
            final AuditEventWriter auditEventWriter) {

        this.appName = appName;
        this.tracer = tracer;
        this.buildProperties = buildProperties;
        this.auditEventWriter = auditEventWriter;
    }

    @Override
    protected void onAuditEvent(AuditEvent event) {
        LOG.debug("On audit event: {}", event);

        try {
            if (AUTHENTICATION_EVENTS.contains(event.getType())) {
                auditEventWriter.write(
                        com.spt.development.audit.spring.AuditEvent.builder()
                                .type(Auditing.Type.SECURITY)
                                .subType(event.getType())
                                .correlationId(
                                    Optional.ofNullable(tracer.currentTraceContext().context())
                                            .map(TraceContext::traceId)
                                            .orElse(null)
                                )
                                .details(GSON.toJson(event.getData()))
                                .username(event.getPrincipal())
                                .originatingIP(getClientIpAddress())
                                .serviceId(appName)
                                .serviceVersion(buildProperties.getVersion())
                                .serverHostName(InetAddress.getLocalHost().getHostName())
                                .created(OffsetDateTime.now(ZoneOffset.UTC))
                                .build()
                );
            }
        } catch (Throwable t) {
            LOG.error("Failed to send audit event: {}", event, t);
        }
    }
}
