package com.spt.development.demo.domain;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

class BookTest {
    private static class TestData {
        static final Long ID = 1L;
        static final String TITLE = "The Hitchhikers Guide to the Galaxy";
        static final String BLURB = "The Hitchhikers Guide to the Galaxy', 'One Thursday lunchtime the Earth gets unexpectedly demolished to make way for a new hyperspace bypass.";
        static final String AUTHOR = "Douglas Adams";
        static final int RRP = 699;
    }

    @Test
    void fromResultSet_validBookResultSet_shouldReturnBook() throws Exception {
        final Book result = Book.fromResultSet(mockBookResultSet());

        assertThat(result).isEqualTo(
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
    void toParameterMap_validBook_shouldReturnParameterMap() {
        final Book target = Book.builder()
                                .id(TestData.ID)
                                .title(TestData.TITLE)
                                .blurb(TestData.BLURB)
                                .author(TestData.AUTHOR)
                                .rrp(TestData.RRP)
                                .build();

        final Map<String, Object> result = target.toParameterMap();

        assertThat(result).containsAllEntriesOf(Map.of(
            "title", TestData.TITLE,
            "blurb", TestData.BLURB,
            "author", TestData.AUTHOR,
            "rrp", TestData.RRP
        ));
    }

    private ResultSet mockBookResultSet() throws SQLException {
        final ResultSet resultSet = Mockito.mock(ResultSet.class);

        when(resultSet.getLong("book_id")).thenReturn(TestData.ID);
        when(resultSet.getString("title")).thenReturn(TestData.TITLE);
        when(resultSet.getString("blurb")).thenReturn(TestData.BLURB);
        when(resultSet.getString("author")).thenReturn(TestData.AUTHOR);
        when(resultSet.getInt("rrp")).thenReturn(TestData.RRP);

        return resultSet;
    }
}