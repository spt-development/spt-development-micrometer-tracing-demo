package com.spt.development.demo.repository;

import com.spt.development.audit.spring.AuditEvent;
import lombok.NonNull;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Repository
public class AuditRepository extends JdbcDaoSupport {
    private static final String SCHEMA = "audit";
    private static final String TABLE = "event";

    private SimpleJdbcInsert simpleJdbcInsert;

    public AuditRepository(final DataSource dataSource) {
        setDataSource(dataSource);
    }

    @Override
    protected void initTemplateConfig() {
        if (simpleJdbcInsert == null) {
            simpleJdbcInsert = new SimpleJdbcInsert(getJdbcTemplate())
                    .withSchemaName(SCHEMA)
                    .withTableName(TABLE);

            simpleJdbcInsert.setGeneratedKeyName("audit_event_id");
        }
    }

    public void create(@NonNull AuditEvent auditEvent) {
        final Map<String, Object> args = new HashMap<>(
                Map.of(
                        "type", auditEvent.getType(),
                        "sub_type", auditEvent.getSubType(),
                        "correlation_id", auditEvent.getCorrelationId(),
                        "service_id", auditEvent.getServiceId(),
                        "service_version", auditEvent.getServiceVersion(),
                        "server_host_name", auditEvent.getServerHostName(),
                        "created", auditEvent.getCreated()
                )
        );

        if (auditEvent.getId() != null) {
            args.put("id", auditEvent.getId());
        }

        if (auditEvent.getDetails() != null) {
            args.put("details", auditEvent.getDetails());
        }

        if (auditEvent.getUserId() != null) {
            args.put("user_id", auditEvent.getUserId());
        }

        if (auditEvent.getUsername() != null) {
            args.put("username", auditEvent.getUsername());
        }

        if (auditEvent.getOriginatingIP() != null) {
            args.put("originating_ip", auditEvent.getOriginatingIP());
        }
        simpleJdbcInsert.execute(args);
    }
}
