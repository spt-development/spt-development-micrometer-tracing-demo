package com.spt.development.demo.service;

import com.spt.development.audit.spring.Audited;
import com.spt.development.cid.CorrelationId;
import com.spt.development.demo.domain.Book;
import com.spt.development.demo.repository.BookRepository;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static com.spt.development.demo.util.Constants.Auditing;

@Slf4j
@Service
@Transactional
@AllArgsConstructor
public class BookService {
    private final BookRepository bookRepository;

    @Audited(type = Auditing.Type.BOOK, subType = Auditing.SubType.CREATED)
    public @Audited.Id("id") Book create(@NonNull @Audited.Detail Book book) {
        return bookRepository.create(book.toBuilder().id(null).build());
    }

    public Optional<Book> read(long id) {
        return bookRepository.read(id);
    }

    public List<Book> readAll() {
        return bookRepository.readAll();
    }

    @Audited(type = Auditing.Type.BOOK, subType = Auditing.SubType.UPDATED)
    public Optional<Book> update(@Audited.Id long id, @NonNull @Audited.Detail Book book) {
        if (id != book.getId()) {
            LOG.warn("[{}] ID on book payload: {}, does not match ID in URL: {}. Using ID from URL",
                    CorrelationId.get(), book.getId(), id);
        }
        return bookRepository.update(book.toBuilder().id(id).build());
    }

    @Audited(type = Auditing.Type.BOOK, subType = Auditing.SubType.DELETED)
    public void delete(@Audited.Id long id) {
        bookRepository.delete(id);
    }
}
