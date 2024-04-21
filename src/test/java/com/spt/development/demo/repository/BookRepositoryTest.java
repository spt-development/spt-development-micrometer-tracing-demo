package com.spt.development.demo.repository;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.test.util.ReflectionTestUtils;

import javax.sql.DataSource;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class BookRepositoryTest {

    @Test
    void setJdbcTemplate_nullTemplateWhenSimpleJdbcInsertNotSet_shouldThrowException() {
        final BookRepositoryArgs args = new BookRepositoryArgs();
        args.simpleJdbcInsert = null;

        final BookRepository target = createRepository(args);

        assertThatThrownBy(() -> target.setJdbcTemplate(null))
            .isExactlyInstanceOf(IllegalStateException.class)
            .hasMessage("JDBC Template must be set");
    }

    private BookRepository createRepository(BookRepositoryArgs args) {
        final BookRepository repository = new BookRepository(args.dataSource);
        repository.init();

        ReflectionTestUtils.setField(repository, "simpleJdbcInsert", args.simpleJdbcInsert);

        return repository;
    }

    private static final class BookRepositoryArgs {
        DataSource dataSource = Mockito.mock(DataSource.class);
        SimpleJdbcInsert simpleJdbcInsert = Mockito.mock(SimpleJdbcInsert.class);
    }
}