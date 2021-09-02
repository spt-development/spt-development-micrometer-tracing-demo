package com.spt.development.demo.domain;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

public record Book(Long id, String title, String blurb, String author, int rrp) {
    public static Book fromResultSet(ResultSet rs) throws SQLException {
        return new Book(
                rs.getLong("book_id"),
                rs.getString("title"),
                rs.getString("blurb"),
                rs.getString("author"),
                rs.getInt("rrp")
        );
    }

    public Map<String, Object> toParameterMap() {
        return Map.of(
                "title", title,
                "blurb", blurb,
                "author", author,
                "rrp", rrp
        );
    }
}
