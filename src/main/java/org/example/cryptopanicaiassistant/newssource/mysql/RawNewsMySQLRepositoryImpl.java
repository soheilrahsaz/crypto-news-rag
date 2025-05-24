package org.example.cryptopanicaiassistant.newssource.mysql;

import lombok.RequiredArgsConstructor;
import org.example.cryptopanicaiassistant.newssource.RawNews;
import org.example.cryptopanicaiassistant.newssource.RawNewsRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class RawNewsMySQLRepositoryImpl implements RawNewsRepository {
    private final JdbcTemplate jdbcTemplate;
    /**
     * only news after 2021 and with more than 3 user votes have `sourceURL` almost 24k rows
     */
    private static final String FIND_ALL_QUERY = """
            SELECT t1.id,
                   t1.title,
                   IFNULL(NULLIF(t1.description, '-'), t1.title) AS description,
                   t1.newsDatetime,
                   t1.url,
                   t1.positive AS positiveVotes,
                   t1.negative AS negativeVotes,
                   t1.sourceUrl,
                   ifnull((SELECT GROUP_CONCAT(tt1.code SEPARATOR ',') FROM currency tt1 
                                          JOIN news__currency tt2 ON tt1.id = tt2.currencyId
                                          WHERE tt2.newsId = t1.id), '') as currencies
            FROM cryptopanic_news t1
            WHERE t1.sourceUrl IS NOT NULL
            """;


    @Override
    public List<RawNews> findAll() {
        return findAll(null);
    }

    @Override
    public List<RawNews> findAll(Long limit) {
        return jdbcTemplate.query(FIND_ALL_QUERY
                + (limit == null ? "" : " LIMIT " + limit), new RawNewsMySQLRowMapper());
    }

    @Override
    public RawNews findById(int id) {
        return jdbcTemplate.queryForObject(FIND_ALL_QUERY + " AND t1.id= ?", new RawNewsMySQLRowMapper(), id);
    }
}
