package org.example.cryptopanicaiassistant.newssource.mysql;

import org.example.cryptopanicaiassistant.newssource.RawNews;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Arrays;

public class RawNewsMySQLRowMapper implements RowMapper<RawNews> {
    @Override
    public RawNews mapRow(ResultSet rs, int rowNum) throws SQLException {
        return RawNews.builder()
                .id(rs.getInt("id"))
                .title(rs.getString("title"))
                .description(rs.getString("description"))
                .newsDatetime(Timestamp.valueOf(rs.getString("newsDatetime")))
                .positiveVotes(rs.getInt("positiveVotes"))
                .negativeVotes(rs.getInt("negativeVotes"))
                .url(rs.getString("url"))
                .sourceUrl(rs.getString("sourceUrl"))
                .currencies(Arrays.stream(rs.getString("currencies").split(",")).filter(code -> !code.isBlank()).toList())
                .build();
    }
}
