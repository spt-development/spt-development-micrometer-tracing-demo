package com.spt.development.demo.domain;

import lombok.Builder;
import lombok.Value;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.collections4.keyvalue.DefaultMapEntry;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Value
@Builder(toBuilder = true)
public class Book {
    Long id;
    String title;
    String blurb;
    String author;
    int rrp;

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
        return Collections.unmodifiableMap(
                MapUtils.putAll(new HashMap<>(), new Object[] {
                        new DefaultMapEntry<>("title", title),
                        new DefaultMapEntry<>("blurb", blurb),
                        new DefaultMapEntry<>("author", author),
                        new DefaultMapEntry<>("rrp", rrp)
                })
        );
    }
}
