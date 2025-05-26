package org.example.cryptopanicaiassistant.newssource.mysql;

import org.example.cryptopanicaiassistant.newssource.RawNews;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.sql.Timestamp;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class RawNewsMySQLRepositoryImplTest {

    @Autowired
    RawNewsMySQLRepositoryImpl rawNewsMySQLRepositoryImpl;

    @Test
    void testFindAll() {
        List<RawNews> rawNews = rawNewsMySQLRepositoryImpl.findAll();
        assertThat(rawNews).isNotEmpty();
    }

    @Test
    void testFindOne() {
        RawNews rawNews = rawNewsMySQLRepositoryImpl.findById(223726);
        assertThat(rawNews).isNotNull();
        assertThat(rawNews.getTitle()).isNotNull();
        assertThat(rawNews.getDescription()).isNotNull();
        assertThat(rawNews.getSourceUrl()).isNotNull();
        assertThat(rawNews.getUrl()).isNotNull();
        assertThat(rawNews.getPositiveVotes()).isEqualTo(2);
        assertThat(rawNews.getNegativeVotes()).isZero();
        assertThat(rawNews.getCurrencies()).containsExactlyInAnyOrder("BTC", "ETH");
        assertThat(rawNews.getNewsDatetime()).isEqualTo(Timestamp.valueOf("2025-05-10 06:30:27"));
    }
}