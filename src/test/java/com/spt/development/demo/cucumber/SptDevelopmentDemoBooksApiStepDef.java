package com.spt.development.demo.cucumber;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.spt.development.demo.domain.Book;
import com.spt.development.demo.repository.BookRepository;
import com.spt.development.test.integration.HttpTestManager;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.ContentType;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;
import java.util.NoSuchElementException;

import static com.spt.development.cid.web.filter.CorrelationIdFilter.CID_HEADER;
import static com.spt.development.test.integration.HttpTestManager.basicCredentialsProvider;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

public class SptDevelopmentDemoBooksApiStepDef {
    private static final Gson GSON = new GsonBuilder().create();

    private interface TestData extends SptDevelopmentDemoStepDef.TestData {
    }

    @Autowired private HttpTestManager httpTestManager;
    @Autowired private BookRepository bookRepository;

    @When("a new book is POSTed to the books REST API")
    public void aNewBookIsPOSTedToTheBooksRESTAPI() throws Throwable {
        httpTestManager.doPostRequest(
                TestData.Api.USERNAME, TestData.Api.PASSWORD, TestData.ValidJob.RESOURCE, ContentType.APPLICATION_JSON, "/api/v1.0/books"
        );
    }

    @When("the last created book is read with a GET request to the books REST API and the correlation ID is set in the request header")
    public void theLastCreatedBookIsReadWithAGETRequestToTheBooksRESTAPIAndTheCorrelationIDIsSetInTheRequestHeader() throws Throwable {
        final long lastCreatedBookId = getLastCreatedBookId();

        final HttpGet httpGet = new HttpGet();
        httpGet.addHeader(CID_HEADER, TestData.CORRELATION_ID);

        httpTestManager.doRequest(
                basicCredentialsProvider(TestData.Api.USERNAME, TestData.Api.PASSWORD), httpGet,
                String.format("/api/v1.0/books/%d", lastCreatedBookId)
        );
    }

    @Then("the response body will contain the new book details")
    public void theResponseBodyWillContainTheNewBookDetails() {
        assertThatResponseContainsValidBook(null);
    }

    @Then("the response will contain the last created book details")
    public void theResponseWillContainTheLastCreatedBookDetails() {
        assertThatResponseContainsValidBook(getLastCreatedBookId());
    }

    private void assertThatResponseContainsValidBook(Long id) {
        final Map<String, Object> book = GSON.fromJson(
                httpTestManager.getResponseBody(), new TypeToken<Map<String, Object>>(){}.getType()
        );

        assertThat(book, is(notNullValue()));

        if (id == null) {
            assertThat(book.get("id"), is(notNullValue()));
        }
        else {
            assertThat(((Double)book.get("id")).longValue(), is(id));
        }
        assertThat(book.get("title"), is(TestData.ValidJob.TITLE));
        assertThat(book.get("blurb"), is(TestData.ValidJob.BLURB));
        assertThat(book.get("author"), is(TestData.ValidJob.AUTHOR));
        assertThat(((Double)book.get("rrp")).intValue(), is(TestData.ValidJob.RRP));
    }

    private long getLastCreatedBookId() {
        return bookRepository.readAll().stream()
                .sorted((b1, b2) -> Long.compare(b2.getId(), b1.getId()))
                .map(Book::getId)
                .findFirst()
                .orElseThrow(NoSuchElementException::new);
    }
}
