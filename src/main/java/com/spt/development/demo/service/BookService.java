package com.spt.development.demo.service;

import com.spt.development.demo.domain.Book;
import com.spt.development.demo.repository.BookRepository;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class BookService {
    private final BookRepository bookRepository;

    public Book create(@NonNull Book book) {
        return bookRepository.create(book.toBuilder().id(null).build());
    }

    public Optional<Book> read(long id) {
        return bookRepository.read(id);
    }

    public List<Book> readAll() {
        return bookRepository.readAll();
    }

    public Optional<Book> update(long id, @NonNull Book book) {
        return bookRepository.update(book.toBuilder().id(id).build());
    }

    public void delete(long id) {
        bookRepository.delete(id);
    }
}
