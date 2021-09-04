package com.spt.development.demo.mvc.controller;

import com.spt.development.demo.domain.Book;
import com.spt.development.demo.service.BookService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("api/v1.0/books")
public class BookController {
    private final BookService bookService;

    @PostMapping
    public ResponseEntity<Book> create(@RequestBody Book book) {
        return ResponseEntity.ok(bookService.create(book));
    }

    @GetMapping("/{id:\\d+}")
    public ResponseEntity<Book> read(@PathVariable long id) {
        return ResponseEntity.of(bookService.read(id));
    }

    @GetMapping
    public ResponseEntity<List<Book>> readAll() {
        return ResponseEntity.ok(bookService.readAll());
    }

    @PutMapping("/{id:\\d+}")
    public ResponseEntity<Book> update(@PathVariable long id, @RequestBody Book book) {
        return ResponseEntity.of(bookService.update(id, book));
    }

    @DeleteMapping("/{id:\\d+}")
    public ResponseEntity<Void> delete(@PathVariable long id) {
        bookService.delete(id);
        return ResponseEntity.ok().build();
    }
}
