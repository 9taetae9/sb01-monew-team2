package com.codeit.team2.monew.module.domain.article.mapper;

import com.codeit.team2.monew.module.domain.article.entity.DummyArticle;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

@Component
public class DummyArticleRowMapper implements RowMapper<DummyArticle> {

    @Override
    public DummyArticle mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new DummyArticle(
            rs.getObject("id", UUID.class),
            rs.getString("title"),
            rs.getString("source"),
            rs.getString("source_url"),
            rs.getString("summary"),
            rs.getObject("view_count", Long.class),
            rs.getTimestamp("published_date") != null
                ? rs.getTimestamp("published_date").toInstant()
                : null,
            rs.getObject("deleted", Boolean.class),
            rs.getString("body_tsv")
        );
    }
}
