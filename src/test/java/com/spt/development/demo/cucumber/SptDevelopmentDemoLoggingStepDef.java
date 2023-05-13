package com.spt.development.demo.cucumber;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Appender;
import com.spt.development.test.integration.HttpTestManager;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.Then;
import org.apache.commons.lang3.StringUtils;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.function.Predicate;
import java.util.regex.Pattern;

import static com.spt.development.demo.config.WebConfig.TRACE_ID_HEADER;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;

public class SptDevelopmentDemoLoggingStepDef {
    private static final String MDC_TRACE_ID_KEY = "traceId";

    @Value("${spt.cid.mdc.disabled:false}")
    private boolean mdcDisabled;

    @Autowired private HttpTestManager httpTestManager;

    private Appender<ILoggingEvent> appender;

    @Before
    public void setUp() {
        appender = createMockAppender();
        getSptDemoLogger().addAppender(appender);
    }

    @SuppressWarnings("unchecked")
    private static Appender<ILoggingEvent> createMockAppender() {
        return Mockito.mock(Appender.class);
    }

    @After
    public void tearDown() {
        getSptDemoLogger().detachAppender(appender);
    }

    private Logger getSptDemoLogger() {
        final LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
        return loggerContext.getLogger("com.spt.development.demo");
    }

    @Then("the book creation is logged at all tiers")
    public void theBookCreationIsLoggedAtAllTiers() {
        final String traceId = httpTestManager.getResponseHeaderValue(TRACE_ID_HEADER).get();

        assertThatMessageIsLogged(Level.INFO, traceId, "BookController.create(");
        assertThatMessageIsLogged(Level.DEBUG, traceId, "BookService.create(");
        assertThatMessageIsLogged(Level.DEBUG, traceId, "BookRepository.create(");
        assertThatMessageIsLogged(Level.TRACE, traceId, "BookRepository.create Returned: Book");
        assertThatMessageIsLogged(Level.TRACE, traceId, "BookService.create Returned: Book");
        assertThatMessageIsLogged(Level.TRACE, traceId, "BookController.create Returned: <201");
    }

    @Then("the book read is logged at all tiers")
    public void theBookReadIsLoggedAtAllTiers() {
        final String traceId = httpTestManager.getResponseHeaderValue(TRACE_ID_HEADER).get();

        assertThatMessageIsLogged(Level.INFO, traceId, "BookController.read(");
        assertThatMessageIsLogged(Level.DEBUG, traceId, "BookService.read(");
        assertThatMessageIsLogged(Level.DEBUG, traceId, "BookRepository.read(");
        assertThatMessageIsLogged(Level.TRACE, traceId, "BookRepository.read Returned: Optional[Book");
        assertThatMessageIsLogged(Level.TRACE, traceId, "BookService.read Returned: Optional[Book");
        assertThatMessageIsLogged(Level.TRACE, traceId, "BookController.read Returned: <200");
    }

    @Then("the unsuccessful book read is logged at all tiers")
    public void theUnsuccessfulBookReadIsLoggedAtAllTiers() {
        final String traceId = httpTestManager.getResponseHeaderValue(TRACE_ID_HEADER).get();

        assertThatMessageIsLogged(Level.INFO, traceId, "BookController.read(");
        assertThatMessageIsLogged(Level.DEBUG, traceId, "BookService.read(");
        assertThatMessageIsLogged(Level.DEBUG, traceId, "BookRepository.read(");
        assertThatMessageIsLogged(Level.TRACE, traceId, "BookRepository.read Returned: Optional.empty");
        assertThatMessageIsLogged(Level.TRACE, traceId, "BookService.read Returned: Optional.empty");
        assertThatMessageIsLogged(Level.TRACE, traceId, "BookController.read Returned: <404");
    }

    @Then("the book read all is logged at all tiers")
    public void theBookReadAllIsLoggedAtAllTiers() {
        final String traceId = httpTestManager.getResponseHeaderValue(TRACE_ID_HEADER).get();

        assertThatMessageIsLogged(Level.INFO, traceId, "BookController.readAll(");
        assertThatMessageIsLogged(Level.DEBUG, traceId, "BookService.readAll(");
        assertThatMessageIsLogged(Level.DEBUG, traceId, "BookRepository.readAll(");
        assertThatMessageIsLogged(Level.TRACE, traceId, "BookRepository.readAll Returned: [Book");
        assertThatMessageIsLogged(Level.TRACE, traceId, "BookService.readAll Returned: [Book");
        assertThatMessageIsLogged(Level.TRACE, traceId, "BookController.readAll Returned: <200");
    }

    @Then("the book update is logged at all tiers")
    public void theBookUpdateIsLoggedAtAllTiers() {
        final String traceId = httpTestManager.getResponseHeaderValue(TRACE_ID_HEADER).get();

        assertThatMessageIsLogged(Level.INFO, traceId, "BookController.update(");
        assertThatMessageIsLogged(Level.DEBUG, traceId, "BookService.update(");
        assertThatMessageIsLogged(Level.DEBUG, traceId, "BookRepository.update(");
        assertThatMessageIsLogged(Level.TRACE, traceId, "BookRepository.update Returned: Optional[Book");
        assertThatMessageIsLogged(Level.TRACE, traceId, "BookService.update Returned: Optional[Book");
        assertThatMessageIsLogged(Level.TRACE, traceId, "BookController.update Returned: <200");
    }

    @Then("the unsuccessful book update is logged at all tiers")
    public void theUnsuccessfulBookUpdateIsLoggedAtAllTiers() {
        final String traceId = httpTestManager.getResponseHeaderValue(TRACE_ID_HEADER).get();

        assertThatMessageIsLogged(Level.INFO, traceId, "BookController.update(");
        assertThatMessageIsLogged(Level.DEBUG, traceId, "BookService.update(");
        assertThatMessageIsLogged(Level.DEBUG, traceId, "BookRepository.update(");
        assertThatMessageIsLogged(Level.TRACE, traceId, "BookRepository.update Returned: Optional.empty");
        assertThatMessageIsLogged(Level.TRACE, traceId, "BookService.update Returned: Optional.empty");
        assertThatMessageIsLogged(Level.TRACE, traceId, "BookController.update Returned: <404");
    }

    @Then("the book delete is logged at all tiers")
    public void theBookDeleteIsLoggedAtAllTiers() {
        final String traceId = httpTestManager.getResponseHeaderValue(TRACE_ID_HEADER).get();

        assertThatMessageIsLogged(Level.INFO, traceId, "BookController.delete(");
        assertThatMessageIsLogged(Level.DEBUG, traceId, "BookService.delete(");
        assertThatMessageIsLogged(Level.DEBUG, traceId, "BookRepository.delete(");
        assertThatMessageIsLogged(Level.DEBUG, traceId, "BookRepository.delete - complete");
        assertThatMessageIsLogged(Level.DEBUG, traceId, "BookService.delete - complete");
        assertThatMessageIsLogged(Level.TRACE, traceId, "BookController.delete Returned: <204");
    }

    @Then("the successful login audit event processing is logged at all tiers")
    public void theSuccessfulLoginAuditEventProcessingIsLoggedAtAllTiers() {
        assertThatAuditLogCreationIsLogged("Security", "AUTHENTICATION_SUCCESS");
    }

    @Then("the new book audit event processing is logged at all tiers")
    public void theNewBookAuditEventProcessingIsLoggedAtAllTiers() {
        assertThatAuditLogCreationIsLogged("Book", "CREATED");
    }

    @Then("the update book audit event processing is logged at all tiers")
    public void theUpdateBookAuditEventProcessingIsLoggedAtAllTiers() {
        assertThatAuditLogCreationIsLogged("Book", "UPDATED");
    }

    @Then("the delete book audit event processing is logged at all tiers")
    public void theDeleteBookAuditEventProcessingIsLoggedAtAllTiers() {
        assertThatAuditLogCreationIsLogged("Book", "DELETED");
    }

    public void assertThatAuditLogCreationIsLogged(String type, String subType) {
        final String correlationId = httpTestManager.getResponseHeaderValue(TRACE_ID_HEADER).get();

        assertThatMessageIsLogged(Level.INFO, correlationId,
                String.format("AuditListener.onMessage('%s', '{\"type\":\"%s\",\"subType\":\"%s\"", correlationId, type, subType));

        assertThatMessageIsLogged(Level.DEBUG, correlationId,
                String.format("AuditRepository.create(AuditEvent(type=%s, subType=%s", type, subType));

        assertThatMessageIsLogged(Level.DEBUG, correlationId, "AuditRepository.create - complete");
        assertThatMessageIsLogged(Level.INFO, correlationId, "AuditListener.onMessage - complete");
    }

    private void assertThatMessageIsLogged(Level logLevel, String traceId, String message) {
        final List<ILoggingEvent> loggingEvents = getLoggingEvents();

        // Trace ID *and* spanID expected to be logged, e.g: [645f5da04e4dd6b128087095176a05cf,5b1a2845af71edf3] ...
        final Pattern traceContextRegex = Pattern.compile(String.format("\\%s,[a-f0-9]{16}\\]", traceId));

        final Predicate<ILoggingEvent> correlationIdPredicate = mdcDisabled
                ? e -> !e.getMDCPropertyMap().containsKey(MDC_TRACE_ID_KEY) &&
                        traceContextRegex.matcher(e.getFormattedMessage()).matches()
                : e -> e.getMDCPropertyMap().getOrDefault(MDC_TRACE_ID_KEY, StringUtils.EMPTY).equals(traceId) &&
                       !traceContextRegex.matcher(e.getFormattedMessage()).matches();

        loggingEvents.stream()
                .filter(e -> e.getLevel().equals(logLevel))
                .filter(correlationIdPredicate)
                .filter(e -> e.getFormattedMessage().contains(message))
                .findFirst()
                .orElseThrow(NoSuchElementException::new);
    }

    private List<ILoggingEvent> getLoggingEvents() {
        final ArgumentCaptor<ILoggingEvent> eventCaptor = ArgumentCaptor.forClass(ILoggingEvent.class);
        verify(appender, atLeastOnce()).doAppend(eventCaptor.capture());

        return eventCaptor.getAllValues();
    }
}
