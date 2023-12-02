package com.spt.development.demo.cucumber;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.spt.development.demo.domain.Book;
import com.spt.development.demo.repository.BookRepository;
import com.spt.development.test.integration.HttpTestManager;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpUriRequestBase;
import org.apache.hc.core5.http.ContentType;
import org.springframework.beans.factory.annotation.Autowired;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.function.Function;

import static com.spt.development.test.integration.HttpTestManager.basicCredentialsProvider;
import static org.assertj.core.api.Assertions.assertThat;

public class SptDevelopmentDemoBooksApiStepDef {
    private static final Gson GSON = new GsonBuilder().create();

    private static final String TRACE_PARENT_HEADER = "traceparent";

    private static class TestData extends SptDevelopmentDemoStepDef.TestData {
    }

    @Autowired private HttpTestManager httpTestManager;
    @Autowired private BookRepository bookRepository;

    @When("a new book is POSTed to the books REST API")
    public void aNewBookIsPOSTedToTheBooksRESTAPI() throws Throwable {
        httpTestManager.doPostRequest(
                TestData.Api.USERNAME, TestData.Api.PASSWORD, TestData.ValidJob.RESOURCE, ContentType.APPLICATION_JSON, "/api/v1.0/books"
        );
    }

    @When("the last created book is read with a GET request to the books REST API and a traceparent with the traceId is set in the request header")
    public void theLastCreatedBookIsReadWithAGETRequestToTheBooksRESTAPIAndATraceParentWithTheTraceIDIsSetInTheRequestHeader() throws Throwable {
        final long lastCreatedBookId = getLastCreatedBookId();

        final Function<URI, HttpUriRequestBase> requestFactory = (uri) -> {
            final HttpGet request = new HttpGet(uri);
            request.addHeader(TRACE_PARENT_HEADER, TestData.TRACE_PARENT);

            return request;
        };

        httpTestManager.doRequest(
                basicCredentialsProvider(TestData.Api.USERNAME, TestData.Api.PASSWORD), requestFactory,
                String.format("/api/v1.0/books/%d", lastCreatedBookId)
        );
    }

    @When("a book with an unknown ID is read with a GET request")
    public void aBookWithAnUnknownIDIsReadWithAGETRequest() throws Throwable {
        httpTestManager.doGetRequest(
                TestData.Api.USERNAME, TestData.Api.PASSWORD, "/api/v1.0/books/999"
        );
    }

    @When("all books are read with a GET request")
    public void allBooksAreReadWithAGETRequest() throws Throwable {
        httpTestManager.doGetRequest(TestData.Api.USERNAME, TestData.Api.PASSWORD, "/api/v1.0/books");
    }

    @When("the last created book is updated with a PUT request to the books REST API")
    public void theLastCreatedBookIsUpdatedWithAPUTRequestToTheBooksRESTAPI() throws Throwable {
        final long lastCreatedBookId = getLastCreatedBookId();

        httpTestManager.doPutRequest(
                TestData.Api.USERNAME, TestData.Api.PASSWORD, TestData.UpdatedJob.RESOURCE, ContentType.APPLICATION_JSON,
                String.format("/api/v1.0/books/%d", lastCreatedBookId)
        );
    }

    @When("a book with an unknown ID is updated with a PUT request to the books REST API")
    public void aBookWithAnUnknownIDIsUpdatedWithAPUTRequestToTheBooksRESTAPI() throws Throwable {
        httpTestManager.doPutRequest(
                TestData.Api.USERNAME, TestData.Api.PASSWORD, TestData.UpdatedJob.RESOURCE, ContentType.APPLICATION_JSON,
                "/api/v1.0/books/999"
        );
    }

    @When("the last created book is deleted with a DELETE request to the books REST API")
    public void theLastCreatedBookIsDeletedWithADELETERequestToTheBooksRESTAPI() throws Throwable {
        final long lastCreatedBookId = getLastCreatedBookId();

        httpTestManager.doDeleteRequest(
                TestData.Api.USERNAME, TestData.Api.PASSWORD, String.format("/api/v1.0/books/%d", lastCreatedBookId)
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

    @Then("the response will contain all books")
    public void theResponseWillContainAllBooks() {
        assertThatResponseContainsAListOfBooks(getLastCreatedBookId());
    }

    @Then("the response will contain the updated book details")
    public void theResponseWillContainTheUpdatedBookDetails() {
        assertThatResponseContainsUpdatedBook(getLastCreatedBookId());
    }

    @Then("the response body will be empty")
    public void theResponseBodyWillBeEmpty() {
        assertThat(httpTestManager.getResponseBody()).isNullOrEmpty();
    }

    private long getLastCreatedBookId() {
        return bookRepository.readAll().stream()
                .sorted((b1, b2) -> Long.compare(b2.getId(), b1.getId()))
                .map(Book::getId)
                .findFirst()
                .orElseThrow(NoSuchElementException::new);
    }

    private void assertThatResponseContainsValidBook(Long id) {
        final Map<String, Object> book = assertThatResponseContainsBook(id);

        assertThat(book.get("title")).isEqualTo(TestData.ValidJob.TITLE);
        assertThat(book.get("blurb")).isEqualTo(TestData.ValidJob.BLURB);
        assertThat(book.get("author")).isEqualTo(TestData.ValidJob.AUTHOR);
        assertThat(((Double)book.get("rrp")).intValue()).isEqualTo(TestData.ValidJob.RRP);
    }

    private void assertThatResponseContainsAListOfBooks(Long id) {
        final List<Map<String, Object>> books = GSON.fromJson(
                httpTestManager.getResponseBody(), new ListOfMapStringObjectTypeToken().getType()
        );
        assertThat(books).isNotEmpty();

        if (id != null) {
            assertThatBookIsExpected(
                    id, books.stream().filter(b -> id.equals(((Double)b.get("id")).longValue())).findFirst().orElse(null)
            );
        }
    }

    private void assertThatResponseContainsUpdatedBook(Long id) {
        final Map<String, Object> book = assertThatResponseContainsBook(id);

        assertThat(book.get("title")).isEqualTo(TestData.UpdatedJob.TITLE);
        assertThat(book.get("blurb")).isEqualTo(TestData.UpdatedJob.BLURB);
        assertThat(book.get("author")).isEqualTo(TestData.UpdatedJob.AUTHOR);
        assertThat(((Double)book.get("rrp")).intValue()).isEqualTo(TestData.UpdatedJob.RRP);
    }

    private Map<String, Object> assertThatResponseContainsBook(Long id) {
        final Map<String, Object> book = GSON.fromJson(
                httpTestManager.getResponseBody(), new MapStringObjectTypeToken().getType()
        );
        assertThatBookIsExpected(id, book);

        return book;
    }

    private void assertThatBookIsExpected(Long id, Map<String, Object> book) {
        assertThat(book).isNotNull();

        if (id == null) {
            assertThat(book.get("id")).isNotNull();
        }
        else {
            assertThat(((Double)book.get("id")).longValue()).isEqualTo(id);
        }
    }

    public static class MapStringObjectTypeToken extends TypeToken<Map<String,Object>> {
        static final long serialVersionUID = 1L;
    }

    public static class ListOfMapStringObjectTypeToken extends TypeToken<List<Map<String,Object>>> {
        static final long serialVersionUID = 1L;
    }
}
