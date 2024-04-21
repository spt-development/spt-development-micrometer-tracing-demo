package com.spt.development.demo.cucumber;

import com.spt.development.demo.domain.Book;
import com.spt.development.demo.repository.BookRepository;
import com.spt.development.test.integration.HttpTestManager;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.NoSuchElementException;

import static com.spt.development.demo.cucumber.SptDevelopmentDemoStepDef.getBookIdFromResponse;
import static org.assertj.core.api.Assertions.assertThat;

public class SptDevelopmentDemoBookDatabaseStepDef {
    private static final class TestData extends SptDevelopmentDemoStepDef.TestData {
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

        assertThat(book).isNotNull();
        assertThat(book.getId()).isEqualTo(bookId);
        assertThat(book.getTitle()).isEqualTo(TestData.ValidJob.TITLE);
        assertThat(book.getBlurb()).isEqualTo(TestData.ValidJob.BLURB);
        assertThat(book.getAuthor()).isEqualTo(TestData.ValidJob.AUTHOR);
        assertThat(book.getRrp()).isEqualTo(TestData.ValidJob.RRP);
    }

    @Then("the last created book will be updated in the database")
    public void theLastCreatedBookWillBeUpdatedInTheDatabase() {
        final long bookId = getBookIdFromResponse(httpTestManager);

        final Book book = bookRepository.read(bookId).orElseThrow(NoSuchElementException::new);

        assertThat(book).isNotNull();
        assertThat(book.getId()).isEqualTo(bookId);
        assertThat(book.getTitle()).isEqualTo(TestData.UpdatedJob.TITLE);
        assertThat(book.getBlurb()).isEqualTo(TestData.UpdatedJob.BLURB);
        assertThat(book.getAuthor()).isEqualTo(TestData.UpdatedJob.AUTHOR);
        assertThat(book.getRrp()).isEqualTo(TestData.UpdatedJob.RRP);
    }

    @Then("the last created book will be deleted from the database")
    public void theLastCreatedBookWillBeDeletedFromTheDatabase() {
        final List<Book> books = bookRepository.readAll();

        assertThat(books).isEmpty();
    }
}
