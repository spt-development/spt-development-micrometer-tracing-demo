package com.spt.development.demo.service;

import com.spt.development.demo.domain.Book;
import com.spt.development.demo.repository.BookRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static com.spt.development.test.LogbackUtil.verifyWarnLogging;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class BookServiceTest {
    private static final class TestData {
        static final Long ID = 1L;
        static final String TITLE = "The Hitchhikers Guide to the Galaxy";
        static final String BLURB = "The Hitchhikers Guide to the Galaxy', 'One Thursday lunchtime the Earth gets unexpectedly demolished to make way for a new hyperspace bypass.";
        static final String AUTHOR = "Douglas Adams";
        static final int RRP = 699;
    }

    @Test
    void create_validBook_shouldReturnCreatedBook() {
        final Book book = Book.builder()
            .id(TestData.ID)
            .title(TestData.TITLE)
            .blurb(TestData.BLURB)
            .author(TestData.AUTHOR)
            .rrp(TestData.RRP)
            .build();

        final Book result = createService().create(book);

        assertThat(result)
            .isNotSameAs(book)
            .usingRecursiveComparison()
            .ignoringFields("id")
            .isEqualTo(book);

        assertThat(result.getId()).isNotNull();
        assertThat(result.getId()).isNotEqualTo(TestData.ID);
    }

    @Test
    void read_existingBookId_shouldReturnBook() {
        final Optional<Book> result = createService().read(TestData.ID);

        assertThat(result).isPresent().contains(
            Book.builder()
                .id(TestData.ID)
                .title(TestData.TITLE)
                .blurb(TestData.BLURB)
                .author(TestData.AUTHOR)
                .rrp(TestData.RRP)
                .build()
        );
    }

    @Test
    void readAll_existingBooks_shouldReturnAllBooks() {
        final List<Book> result = createService().readAll();

        assertThat(result).containsExactly(
            Book.builder()
                .id(TestData.ID)
                .title(TestData.TITLE)
                .blurb(TestData.BLURB)
                .author(TestData.AUTHOR)
                .rrp(TestData.RRP)
                .build()
        );
    }

    @Test
    void update_existingBook_shouldReturnUpdatedBook() {
        final Book book = Book.builder()
                              .id(TestData.ID + 1) // ID will be ignored
                              .title(TestData.TITLE + " updated")
                              .blurb(TestData.BLURB)
                              .author(TestData.AUTHOR)
                              .rrp(TestData.RRP)
                              .build();

        final Optional<Book> result = createService().update(TestData.ID, book);

        assertThat(result).isPresent().contains(
            Book.builder()
                .id(TestData.ID)
                .title(TestData.TITLE + " updated")
                .blurb(TestData.BLURB)
                .author(TestData.AUTHOR)
                .rrp(TestData.RRP)
                .build()
        );
    }

    @Test
    void update_differentIDs_shouldLogWarning() {
        final BookService target = createService();

        final Book book = Book.builder()
                              .id(TestData.ID + 1) // ID will be ignored
                              .title(TestData.TITLE + " updated")
                              .blurb(TestData.BLURB)
                              .author(TestData.AUTHOR)
                              .rrp(TestData.RRP)
                              .build();

        verifyWarnLogging(
            BookService.class,
            () -> target.update(TestData.ID, book),
            String.format("ID on book payload: %d, does not match ID in URL: %d. Using ID from URL", book.getId(), TestData.ID)
        );
    }

    @Test
    void delete_existingId_shouldDelegateToRepository() {
        final BookServiceArgs args = new BookServiceArgs();

        createService(args).delete(TestData.ID);

        verify(args.bookRepository).delete(TestData.ID);
    }

    private BookService createService() {
        return createService(new BookServiceArgs());
    }

    private BookService createService(BookServiceArgs args) {
        return new BookService(args.bookRepository);
    }

    private static class BookServiceArgs {
        BookRepository bookRepository = Mockito.mock(BookRepository.class);

        BookServiceArgs() {
            when(bookRepository.create(any())).thenAnswer(iom -> {
                final Book book = iom.getArgument(0);

                if (book.getId() == null) {
                    ReflectionTestUtils.setField(book, "id", LocalDate.now().toEpochDay());
                }
                return book;
            });

            final Book book = Book.builder()
                                  .id(TestData.ID)
                                  .title(TestData.TITLE)
                                  .blurb(TestData.BLURB)
                                  .author(TestData.AUTHOR)
                                  .rrp(TestData.RRP)
                                  .build();

            when(bookRepository.read(TestData.ID)).thenReturn(Optional.of(book));
            when(bookRepository.readAll()).thenReturn(List.of(book));

            when(bookRepository.update(any())).thenAnswer(iom -> {
                final Book updated = iom.getArgument(0);

                if (updated.getId() == TestData.ID) {
                    return Optional.of(updated);
                }
                return Optional.empty();
            });
        }
    }
}