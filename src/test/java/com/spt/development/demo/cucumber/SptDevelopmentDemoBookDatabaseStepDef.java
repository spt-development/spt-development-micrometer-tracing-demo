package com.spt.development.demo.cucumber;

import com.spt.development.demo.domain.Book;
import com.spt.development.demo.repository.BookRepository;
import com.spt.development.test.integration.HttpTestManager;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.NoSuchElementException;

import static com.spt.development.demo.cucumber.SptDevelopmentDemoStepDef.getBookIdFromResponse;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

public class SptDevelopmentDemoBookDatabaseStepDef {


    private interface TestData extends SptDevelopmentDemoStepDef.TestData {
    }

    @Autowired private HttpTestManager httpTestManager;
    @Autowired private BookRepository bookRepository;

    @Given("a book exists in the database")
    public void aBookExistsInTheDatabase() {
        bookRepository.create(
                Book.builder()
                        .title(TestData.ValidJob.TITLE)
                        .blurb(TestData.ValidJob.BLURB)
                        .author(TestData.ValidJob.AUTHOR)
                        .rrp(TestData.ValidJob.RRP)
                        .build()
        );
    }

    @Then("the new book will be added to the database")
    public void theNewBookWillBeAddedToTheDatabase() {
        final long bookId = getBookIdFromResponse(httpTestManager);

        final Book book = bookRepository.read(bookId).orElseThrow(NoSuchElementException::new);

        assertThat(book, is(notNullValue()));
        assertThat(book.getId(), is(notNullValue()));
        assertThat(book.getTitle(), is(TestData.ValidJob.TITLE));
        assertThat(book.getBlurb(), is(TestData.ValidJob.BLURB));
        assertThat(book.getAuthor(), is(TestData.ValidJob.AUTHOR));
        assertThat(book.getRrp(), is(TestData.ValidJob.RRP));
    }
}
