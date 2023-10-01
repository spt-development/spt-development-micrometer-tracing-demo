package com.spt.development.demo.repository;

import com.spt.development.demo.domain.Book;
import jakarta.annotation.PostConstruct;
import lombok.NonNull;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Repository
public class BookRepository extends JdbcDaoSupport {
    private static final String SCHEMA = "demo";
    private static final String TABLE = "book";

    private final DataSource dataSource;

    private SimpleJdbcInsert simpleJdbcInsert = null;

    public BookRepository(final DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @PostConstruct
    public void init() {
        setDataSource(dataSource);
    }

    @Override
    protected void initTemplateConfig() {
        if (simpleJdbcInsert == null) {
            simpleJdbcInsert = new SimpleJdbcInsert(getJdbcTemplate())
                    .withSchemaName(SCHEMA)
                    .withTableName(TABLE)
                    .usingGeneratedKeyColumns("book_id");
        }
    }

    public Book create(@NonNull Book book) {
        final Number id = simpleJdbcInsert.executeAndReturnKey(book.toParameterMap());
        return book.toBuilder().id(id.longValue()).build();
    }

    public Optional<Book> read(long id) {
        try {
            return Optional.ofNullable(
                    getJdbcTemplate().queryForObject(
                            "SELECT book_id, title, blurb, author, rrp FROM demo.book WHERE book_id = ?",
                            new BookRowMapper(),
                            id
                    )
            );
        }
        catch (EmptyResultDataAccessException ex) {
            return Optional.empty();
        }
    }

    public List<Book> readAll() {
        return getJdbcTemplate().query(
                "SELECT book_id, title, blurb, author, rrp FROM demo.book",
                new BookRowMapper()
        );
    }

    public Optional<Book> update(@NonNull Book book) {
        final int rows = getJdbcTemplate().update(
                "UPDATE demo.book SET title = ?, blurb = ?, author = ?, rrp = ? WHERE book_id = ?",
                book.getTitle(),
                book.getBlurb(),
                book.getAuthor(),
                book.getRrp(),
                book.getId()
        );

        if (rows == 0) {
            return Optional.empty();
        }
        return Optional.of(book);
    }

    public void delete(long id) {
        getJdbcTemplate().update(
                "DELETE FROM demo.book where book_id = ?",
                id
        );
    }

    private static class BookRowMapper implements RowMapper<Book> {

        @Override
        public Book mapRow(ResultSet rs, int rowNum) throws SQLException {
            return Book.fromResultSet(rs);
        }
    }
}
