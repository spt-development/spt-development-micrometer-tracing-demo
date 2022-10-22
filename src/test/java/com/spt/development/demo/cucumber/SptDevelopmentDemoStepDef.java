package com.spt.development.demo.cucumber;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.spt.development.cid.CorrelationId;
import com.spt.development.demo.cucumber.config.TestManagerConfig;
import com.spt.development.demo.cucumber.util.DatabaseTestUtil;
import com.spt.development.test.integration.HttpTestManager;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.spring.CucumberContextConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;
import java.util.Optional;

import static com.spt.development.cid.web.filter.CorrelationIdFilter.CID_HEADER;
import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@CucumberContextConfiguration
@Import(TestManagerConfig.class)
public class SptDevelopmentDemoStepDef {
    private static final Gson GSON = new GsonBuilder().create();

    interface TestData {
        String CORRELATION_ID = "709ac6e1-8ace-422d-9421-b8d93f0c6505";

        interface Resource {
            String ROOT = "/com/spt/development/demo/cucumber/requests/";
        }

        interface Api {
            String USERNAME = "bob";
            String PASSWORD = "password123!";
        }

        interface ValidJob {
            String TITLE = "The Hitchhikers Guide to the Galaxy";
            String BLURB = "The Hitchhikers Guide to the Galaxy', 'One Thursday lunchtime the Earth gets unexpectedly demolished to make way for a new hyperspace bypass.";
            String AUTHOR = "Douglas Adams";
            int RRP = 699;

            String RESOURCE = Resource.ROOT + "valid-book.json";
        }

        interface UpdatedJob {
            String TITLE = ValidJob.TITLE + " (updated)";
            String BLURB = ValidJob.BLURB + " (updated)";
            String AUTHOR = ValidJob.AUTHOR + " (updated)";
            int RRP = ValidJob.RRP + 100;

            String RESOURCE = Resource.ROOT + "updated-book.json";
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

        CorrelationId.set("integration-test-runner-cid");
    }

    private void clearDatabase() {
        try (final Connection connection = dataSource.getConnection()) {
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

    @Then("^the response will have a correlationId header$")
    public void theResponseWillHaveACorrelationIdHeader() {
        final Optional<String> correlationId = httpTestManager.getResponseHeaderValue(CID_HEADER);

        assertThat(correlationId).isPresent();
    }

    @Then("the response will have the correlationID header sent in the request")
    public void theResponseWillHaveTheCorrelationIDHeaderSentInTheRequest() {
        final Optional<String> correlationId = httpTestManager.getResponseHeaderValue(CID_HEADER);

        assertThat(correlationId).contains(TestData.CORRELATION_ID);
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
