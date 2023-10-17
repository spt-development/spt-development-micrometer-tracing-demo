package com.spt.development.demo.cucumber;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.spt.development.demo.cucumber.config.TestManagerConfig;
import com.spt.development.demo.cucumber.util.DatabaseTestUtil;
import com.spt.development.test.integration.HttpTestManager;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import io.cucumber.java.en.Then;
import io.cucumber.spring.CucumberContextConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.actuate.observability.AutoConfigureObservability;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;
import java.util.Optional;

import static com.spt.development.demo.config.WebConfig.TRACE_ID_HEADER;
import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@CucumberContextConfiguration
@Import(TestManagerConfig.class)
@AutoConfigureObservability(metrics = false) // <-- Required to switch on tracing in tests, not required in production code
public class SptDevelopmentDemoStepDef {
    private static final Gson GSON = new GsonBuilder().create();

    static class TestData {
        static final String TRACE_ID = "709ac6e18ace422d9421b8d93f0c6505";
        static final String TRACE_PARENT = String.format("00-%s-1444ca74c3d2133a-01", TRACE_ID);

        static class Resource {
            static final String ROOT = "/com/spt/development/demo/cucumber/requests/";
        }

        static class Api {
            static final String USERNAME = "bob";
            static final String PASSWORD = "password123!";
        }

        static class ValidJob {
            static final String TITLE = "The Hitchhikers Guide to the Galaxy";
            static final String BLURB = "The Hitchhikers Guide to the Galaxy', 'One Thursday lunchtime the Earth gets unexpectedly demolished to make way for a new hyperspace bypass.";
            static final String AUTHOR = "Douglas Adams";
            static final int RRP = 699;

            static final String RESOURCE = Resource.ROOT + "valid-book.json";
        }

        static class UpdatedJob {
            static final String TITLE = ValidJob.TITLE + " (updated)";
            static final String BLURB = ValidJob.BLURB + " (updated)";
            static final String AUTHOR = ValidJob.AUTHOR + " (updated)";
            static final int RRP = ValidJob.RRP + 100;

            static final String RESOURCE = Resource.ROOT + "updated-book.json";
        }
    }

    @LocalServerPort private int localServerPort;

    @Autowired private DataSource dataSource;
    @Autowired private HttpTestManager httpTestManager;

    @Before
    public void setUp(Scenario scenario) {
        LOG.info("Running scenario: {}", scenario.getName());

        httpTestManager.init(localServerPort);

        clearDatabase();
    }

    private void clearDatabase() {
        try (Connection connection = dataSource.getConnection()) {
            DatabaseTestUtil.clearDatabase(connection);
        }
        catch (SQLException ex) {
            LOG.warn("Failed to clear database", ex);
        }
    }

    @After
    public void tearDown(Scenario scenario) {
        LOG.info("Finishing scenario: '{}', with status: {}", scenario.getName(), scenario.getStatus());
    }

    @Then("^the server will respond with a HTTP status of '(\\d+)'$")
    public void theServerWillRespondWithAHTTPStatusOf(int statusCode) {
        assertThat(httpTestManager.getStatusCode()).isEqualTo(statusCode);
    }

    @Then("^the response will have a traceId header$")
    public void theResponseWillHaveATraceIDHeader() {
        final Optional<String> traceId = httpTestManager.getResponseHeaderValue(TRACE_ID_HEADER);

        assertThat(traceId).isPresent();
    }

    @Then("the response will have the traceId header sent in the request")
    public void theResponseWillHaveTheTraceIDHeaderSentInTheRequest() {
        final Optional<String> traceId = httpTestManager.getResponseHeaderValue(TRACE_ID_HEADER);

        assertThat(traceId).contains(TestData.TRACE_ID);
    }

    static long getBookIdFromResponse(HttpTestManager httpTestManager) {
        final Map<String, Object> book = GSON.fromJson(
                httpTestManager.getResponseBody(), new MapStringObjectTypeToken().getType()
        );
        return Double.valueOf(book.get("id").toString()).longValue();
    }

    public static class MapStringObjectTypeToken extends TypeToken<Map<String,Object>> {
        static final long serialVersionUID = 1L;
    }
}
