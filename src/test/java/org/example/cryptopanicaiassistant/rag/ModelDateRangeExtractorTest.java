package org.example.cryptopanicaiassistant.rag;

import org.assertj.core.data.TemporalUnitWithinOffset;
import org.example.cryptopanicaiassistant.rag.ModelDateRangeExtractor.DateRange;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class ModelDateRangeExtractorTest {

    @Autowired
    OllamaChatModel ollamaChatModel;

    @Test
    void testExtractDateForPastWeek() {
        LocalDate now = LocalDate.now();
        ModelDateRangeExtractor modelDateRangeExtractor = ModelDateRangeExtractor.builder()
                .chatClient(ChatClient.builder(ollamaChatModel).build())
                .build();

        DateRange dateRange = modelDateRangeExtractor.extractDateRangeFromUserQuery("what has happened to bitcoin since last week?", now);
        assertThat(dateRange.startDate()).isCloseTo(now.minusDays(7), new TemporalUnitWithinOffset(2, ChronoUnit.DAYS));
        assertThat(dateRange.endDate()).isCloseTo(now, new TemporalUnitWithinOffset(2, ChronoUnit.DAYS));
        System.out.println(dateRange);
    }

    @Test
    void testExtractDateForSpecificDate() {
        LocalDate now = LocalDate.now();
        ModelDateRangeExtractor modelDateRangeExtractor = ModelDateRangeExtractor.builder()
                .chatClient(ChatClient.builder(ollamaChatModel).build())
                .build();

        DateRange dateRange = modelDateRangeExtractor.extractDateRangeFromUserQuery("what has happened to bitcoin in march?", now);
        System.out.println(dateRange);
    }
}