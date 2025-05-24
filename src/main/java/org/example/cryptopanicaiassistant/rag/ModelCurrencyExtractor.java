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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Builder
@AllArgsConstructor
@Slf4j
public class ModelCurrencyExtractor {
    private final ChatClient chatClient;
    private final PromptTemplate promptTemplate = new PromptTemplate("""
            Extract any cryptocurrencies mentioned in the user query. Return a list — one item per line — containing either the abbreviation (e.g., BTC) or the full name (e.g., Bitcoin), whichever you can extract. If no specific cryptocurrencies are mentioned, return a single line with ALL.
            
            Return only the CSV output. No explanations, no headers, no extra text.
            
            Examples:
            
            User query:
            "What is going on with Bitcoin, Ethereum, and Solana?"
            Output:
            Bitcoin
            ETH
            SOL
            
            User query:
            "What’s the market outlook for this year?"
            Output:
            ALL
            
            Now extract for this query:
            {query}
            """);

    public List<String> extractCurrenciesFromUserQuery(@NonNull String query) {
        return extractCurrenciesFromUserQuery(Query.builder()
                .text(query)
                .build());
    }

    /**
     * @return a list of currencies, or an empty list if there is no specific currency
     */
    public List<String> extractCurrenciesFromUserQuery(@NonNull Query query) {
        log.debug("Extracting currencies from user query");

        String response = this.chatClient.prompt()
                .user(user -> user.text(this.promptTemplate.getTemplate())
                        .param("query", query.text()))
                .options(ChatOptions.builder().build())
                .call()
                .content();

        if (!StringUtils.hasText(response) || "ALL".equals(response)) {
            return Collections.emptyList();
        }

        log.debug("Extracted currencies from user query: {}", response);
        return Arrays.stream(response.split("\r\n|\r|\n")).map(String::trim).toList();
    }
}
