package com.spt.development.demo.cucumber;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.spt.development.audit.spring.AuditEvent;
import com.spt.development.test.integration.HttpTestManager;
import io.cucumber.java.en.Then;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.info.BuildProperties;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.spt.development.cid.web.filter.CorrelationIdFilter.CID_HEADER;
import static com.spt.development.demo.cucumber.SptDevelopmentDemoStepDef.MapStringObjectTypeToken;
import static com.spt.development.demo.cucumber.SptDevelopmentDemoStepDef.getBookIdFromResponse;
import static com.spt.development.demo.util.Constants.Auditing;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.springframework.boot.actuate.security.AuthenticationAuditListener.AUTHENTICATION_SUCCESS;

public class SptDevelopmentDemoAuditDatabaseStepDef {
    private static final Gson GSON = new GsonBuilder().create();

    private interface TestData extends SptDevelopmentDemoStepDef.TestData {
    }

    @Value("${spring.application.name}") private String appName;

    @Autowired private JdbcTemplate jdbcTemplate;
    @Autowired private BuildProperties buildProperties;
    @Autowired private HttpTestManager httpTestManager;

    @Then("a successful login audit event will eventually be created")
    public void aSuccessfulLoginAuditEventWillEventuallyBeCreated() {
        final String correlationId = httpTestManager.getResponseHeaderValue(CID_HEADER).get();

        final AuditEvent loginAuditEvent =
                waitForAuditEvents(Auditing.Type.SECURITY, AUTHENTICATION_SUCCESS).stream()
                        .filter(ae -> correlationId.equals(ae.getCorrelationId()))
                        .findFirst()
                        .orElseThrow();

        assertThat(loginAuditEvent.getId(), is(nullValue()));

        final Map<String, Object> details = GSON.fromJson(
                loginAuditEvent.getDetails(), new MapStringObjectTypeToken().getType()
        );
        assertThat(details, is(Map.of("details", Map.of("remoteAddress", "127.0.0.1"))));

        assertCommonAuditEventFields(loginAuditEvent);
    }

    @Then("a new book audit event will eventually be created")
    public void aNewBookAuditEventWillEventuallyBeCreated() {
        final long bookId = getBookIdFromResponse(httpTestManager);
        final String correlationId = httpTestManager.getResponseHeaderValue(CID_HEADER).get();

        final AuditEvent newBookAuditEvent =
                waitForAuditEvents(Auditing.Type.BOOK, Auditing.SubType.CREATED).stream()
                        .filter(ae -> correlationId.equals(ae.getCorrelationId()) &&
                                Optional.ofNullable(ae.getId()).map(id -> Long.parseLong(id) == bookId).orElse(false))
                        .findFirst()
                        .orElseThrow();

        final Map<String, Object> auditEventDetails =
                GSON.fromJson(newBookAuditEvent.getDetails(), new MapStringObjectTypeToken().getType());

        final Map<String, Object> responseBody =
                new HashMap<>(GSON.fromJson(httpTestManager.getResponseBody(), new MapStringObjectTypeToken().getType()));

        responseBody.remove("id"); // ID won't be in details

        assertThat(auditEventDetails, is(responseBody));

        assertCommonAuditEventFields(newBookAuditEvent);
    }

    private List<AuditEvent> waitForAuditEvents(String type, String subType) {
        return await().until(() -> read(type, subType), ae -> !ae.isEmpty());
    }

    public List<AuditEvent> read(String type, String subType) {
        return jdbcTemplate.query(
                "SELECT event_id, type, sub_type, correlation_id, id, details, user_id, username, originating_ip, service_id, service_version, server_host_name, created FROM audit.event WHERE type = ? AND sub_type = ?",
                new AuditEventMapper(),
                type, subType
        );
    }

    private void assertCommonAuditEventFields(AuditEvent auditEvent) {
        assertThat(auditEvent.getUserId(), is(nullValue()));
        assertThat(auditEvent.getUsername(), is(TestData.Api.USERNAME));
        assertThat(auditEvent.getOriginatingIP(), is(notNullValue()));
        assertThat(auditEvent.getServiceId(), is(appName));
        assertThat(auditEvent.getServiceVersion(), is(buildProperties.getVersion()));
        assertThat(auditEvent.getServerHostName(), is(notNullValue()));
        assertThat(auditEvent.getCreated(), is(notNullValue()));
    }

    private static class AuditEventMapper implements RowMapper<AuditEvent> {
        @Override
        public AuditEvent mapRow(ResultSet rs, int rowNum) throws SQLException {
            return AuditEvent.builder()
                    .type(rs.getString("type"))
                    .subType(rs.getString("sub_type"))
                    .correlationId(rs.getString("correlation_id"))
                    .id(rs.getString("id"))
                    .details(rs.getString("details"))
                    .userId(rs.getString("user_id"))
                    .username(rs.getString("username"))
                    .originatingIP(rs.getString("originating_ip"))
                    .serviceId(rs.getString("service_id"))
                    .serviceVersion(rs.getString("service_version"))
                    .serverHostName(rs.getString("server_host_name"))
                    .created(rs.getObject("created", OffsetDateTime.class))
                    .build();
        }
    }
}
