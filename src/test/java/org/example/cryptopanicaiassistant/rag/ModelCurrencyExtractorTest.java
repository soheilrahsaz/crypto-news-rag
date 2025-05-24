package org.example.cryptopanicaiassistant.rag;

import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class ModelCurrencyExtractorTest {

    @Autowired
    OllamaChatModel ollamaChatModel;

    @Test
    void testExtractCurrencies() throws Exception {
        ModelCurrencyExtractor modelCurrencyExtractor = ModelCurrencyExtractor.builder()
                .chatClient(ChatClient.builder(ollamaChatModel).build())
                .build();

        List<String> currencies = modelCurrencyExtractor.extractCurrenciesFromUserQuery("why is elrond coin rising?");
        System.out.println(currencies);
        assertThat(currencies).isNotEmpty();
    }
}