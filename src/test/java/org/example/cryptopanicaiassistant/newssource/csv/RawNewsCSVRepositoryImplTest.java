package org.example.cryptopanicaiassistant.newssource.csv;

import org.example.cryptopanicaiassistant.newssource.RawNews;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class RawNewsCSVRepositoryImplTest {

    @Test
    void findAll() {
        RawNewsCSVRepositoryImpl rawNewsCSVRepository  = new RawNewsCSVRepositoryImpl();
        List<RawNews> all = rawNewsCSVRepository.findAll();
        assertThat(all).isNotEmpty();
    }
}