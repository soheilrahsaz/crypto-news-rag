package org.example.cryptopanicaiassistant;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class TestJDBCTemplate {

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Test
    void testSelect() {
        List<Map<String, Object>> res = jdbcTemplate.queryForList("SELECT t1.id from cryptopanic_news t1 limit 1");
        assertThat(res).hasSize(1);
    }
}
