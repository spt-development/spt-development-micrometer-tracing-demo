package com.spt.development.demo.domain;

import lombok.Builder;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

public record Book(Long id, String title, String blurb, String author, int rrp) {
    @Builder(toBuilder = true)
    public Book {}

    public static Book fromResultSet(ResultSet rs) throws SQLException {
        return Book.builder()
                .id(rs.getLong("book_id"))
                .title(rs.getString("title"))
                .blurb(rs.getString("blurb"))
                .author(rs.getString("author"))
                .rrp(rs.getInt("rrp"))
                .build();
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
