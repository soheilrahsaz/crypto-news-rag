package org.example.cryptopanicaiassistant.rag;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.rag.Query;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

@Builder
@AllArgsConstructor
@Slf4j
public class ModelDateRangeExtractor {
    private final ChatClient chatClient;
    private final PromptTemplate promptTemplate = new PromptTemplate("""
            You are given a natural language query and the current date.
            Your task is to extract the `start` and `end` dates implied in the query. If no specific date is implied, just return UNKNOWN
            Today is: {current_date}
            Current Year is: {current_year}
            
            Input:
            A query
            
            Output:
            Return exactly 2 lines:
            
            1. First line → start date in format `YYYY-MM-DD`
            2. Second line → end date in format `YYYY-MM-DD`
            
            Always return full date values, not expressions or placeholders.
            Do not Return anything else, no explanation, no extra info, just 2 lines.
            
            Examples:
            
            What happened in the last 10 days?
            {10_days_ago}
            {current_date}
            
            What has happened since last week?
            {last_week}
            {current_date}
            
            What happened in March 2024?
            2024-03-01
            2024-03-31
            
            Tell me about x since February
            {current_year}-02-01
            {current_date}
            
            What is the affect of x on y?
            UNKNOWN
            
            Now extract the date range:
            {query}
            """);

    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    public record DateRange(LocalDate startDate, LocalDate endDate) {
    }

    public DateRange extractDateRangeFromUserQuery(@NonNull String query, @NonNull LocalDate currentDate) {
        return extractCurrenciesFromUserQuery(Query.builder()
                .text(query)
                .build(), currentDate);
    }

    public DateRange extractCurrenciesFromUserQuery(@NonNull Query query, @NonNull LocalDate currentDate) {
        log.debug("Extracting date range from user query");

        String response = this.chatClient.prompt()
                .user(user -> user.text(this.promptTemplate.getTemplate())
                        .param("query", query.text())
                        .param("10_days_ago", currentDate.minusDays(10).format(dateTimeFormatter))
                        .param("last_week", currentDate.minusWeeks(1).format(dateTimeFormatter))
                        .param("current_date", currentDate.format(dateTimeFormatter))
                        .param("current_year", currentDate.getYear())
                )
                .options(ChatOptions.builder().build())
                .call()
                .content();

        try {
            if(!StringUtils.hasText(response)){
                throw new IllegalArgumentException("empty response from model");
            }

            log.debug("response for date range: {}", response);
            if(response.contains("UNKNOWN")){
                return null;
            }

            List<String> dates = Arrays.stream(response.split("\r\n|\r|\n")).map(String::trim).toList();
            if(dates.size() <= 1){
                throw new IllegalArgumentException("Invalid date range response: " + response);
            }
            return new DateRange(LocalDate.parse(dates.get(dates.size() - 2), dateTimeFormatter), LocalDate.parse(dates.getLast(), dateTimeFormatter));
        }catch (Exception e){
            log.warn("unable to extract date range, returning default range", e);
            return new DateRange(currentDate.minusMonths(1), currentDate);
        }
    }
}
