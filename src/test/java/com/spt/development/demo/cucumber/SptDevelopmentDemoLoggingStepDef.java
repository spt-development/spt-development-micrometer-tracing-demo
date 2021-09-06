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
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static com.spt.development.cid.web.filter.CorrelationIdFilter.CID_HEADER;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;

public class SptDevelopmentDemoLoggingStepDef {
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
        final String correlationId = httpTestManager.getResponseHeaderValue(CID_HEADER).get();

        assertThatMessageIsLogged(Level.INFO, correlationId, "BookController.create(");
        assertThatMessageIsLogged(Level.DEBUG, correlationId, "BookService.create(");
        assertThatMessageIsLogged(Level.DEBUG, correlationId, "BookRepository.create(");
        assertThatMessageIsLogged(Level.TRACE, correlationId, "BookRepository.create Returned: Book");
        assertThatMessageIsLogged(Level.TRACE, correlationId, "BookService.create Returned: Book");
        assertThatMessageIsLogged(Level.TRACE, correlationId, "BookController.create Returned: <201");
    }

    @Then("the book read is logged at all tiers")
    public void theBookReadIsLoggedAtAllTiers() {
        final String correlationId = httpTestManager.getResponseHeaderValue(CID_HEADER).get();

        assertThatMessageIsLogged(Level.INFO, correlationId, "BookController.read(");
        assertThatMessageIsLogged(Level.DEBUG, correlationId, "BookService.read(");
        assertThatMessageIsLogged(Level.DEBUG, correlationId, "BookRepository.read(");
        assertThatMessageIsLogged(Level.TRACE, correlationId, "BookRepository.read Returned: Optional[Book");
        assertThatMessageIsLogged(Level.TRACE, correlationId, "BookService.read Returned: Optional[Book");
        assertThatMessageIsLogged(Level.TRACE, correlationId, "BookController.read Returned: <200");
    }

    @Then("the successful login audit event processing is logged at all tiers")
    public void theSuccessfulLoginAuditEventProcessingIsLoggedAtAllTiers() {
        final String correlationId = httpTestManager.getResponseHeaderValue(CID_HEADER).get();


        assertThatMessageIsLogged(Level.INFO, correlationId,
                String.format("AuditListener.onMessage('%s', '{\"type\":\"Security\",\"subType\":\"AUTHENTICATION_SUCCESS\"", correlationId));

        assertThatMessageIsLogged(Level.DEBUG, correlationId, "AuditRepository.create(AuditEvent(type=Security, subType=AUTHENTICATION_SUCCESS");
        assertThatMessageIsLogged(Level.DEBUG, correlationId, "AuditRepository.create - complete");
        assertThatMessageIsLogged(Level.INFO, correlationId, "AuditListener.onMessage - complete");
    }

    @Then("the new book audit event processing is logged at all tiers")
    public void theNewBookAuditEventProcessingIsLoggedAtAllTiers() {
        final String correlationId = httpTestManager.getResponseHeaderValue(CID_HEADER).get();

        assertThatMessageIsLogged(Level.INFO, correlationId,
                String.format("AuditListener.onMessage('%s', '{\"type\":\"Book\",\"subType\":\"CREATED\"", correlationId));

        assertThatMessageIsLogged(Level.DEBUG, correlationId, "AuditRepository.create(AuditEvent(type=Book, subType=CREATED");
        assertThatMessageIsLogged(Level.DEBUG, correlationId, "AuditRepository.create - complete");
        assertThatMessageIsLogged(Level.INFO, correlationId, "AuditListener.onMessage - complete");
    }

    private void assertThatMessageIsLogged(Level logLevel, String correlationId, String message) {
        final List<ILoggingEvent> loggingEvents = getLoggingEvents();

        loggingEvents.stream()
                .filter(e -> e.getLevel().equals(logLevel) &&
                        e.getFormattedMessage().contains(String.format("[%s] %s", correlationId, message)))
                .findFirst()
                .orElseThrow();
    }

    private List<ILoggingEvent> getLoggingEvents() {
        final ArgumentCaptor<ILoggingEvent> eventCaptor = ArgumentCaptor.forClass(ILoggingEvent.class);
        verify(appender, atLeastOnce()).doAppend(eventCaptor.capture());

        return eventCaptor.getAllValues();
    }
}
