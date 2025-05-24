package org.example.cryptopanicaiassistant.rag;


import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.rag.advisor.RetrievalAugmentationAdvisor;
import org.springframework.ai.rag.generation.augmentation.ContextualQueryAugmenter;
import org.springframework.ai.rag.retrieval.search.VectorStoreDocumentRetriever;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;


@Service
public class CryptoAIAssistService {
    private final RetrievalAugmentationAdvisor retrievalAugmentationAdvisor;
    private final ChatClient chatClient;
    private final ModelDateRangeExtractor modelDateRangeExtractor;
    private final ModelCurrencyExtractor modelCurrencyExtractor;
    private final StaticCurrencyExtractor staticCurrencyExtractor;

    public CryptoAIAssistService(VectorStore vectorStore, ChatModel chatModel) {
        this.retrievalAugmentationAdvisor = RetrievalAugmentationAdvisor.builder()
                .documentRetriever(VectorStoreDocumentRetriever.builder()
                        .vectorStore(vectorStore)
                        .topK(5)
                        .similarityThreshold(0.7d)
                        .build())
                .queryAugmenter(ContextualQueryAugmenter.builder().build())
                .build();
        this.chatClient = ChatClient.builder(chatModel).build();
        this.modelDateRangeExtractor = ModelDateRangeExtractor.builder()
                .chatClient(chatClient)
                .build();
        this.modelCurrencyExtractor = ModelCurrencyExtractor.builder()
                .chatClient(chatClient)
                .build();
        this.staticCurrencyExtractor = new StaticCurrencyExtractor();
    }

    public String ask(String userQuery){

        ModelDateRangeExtractor.DateRange dateRange = modelDateRangeExtractor.extractDateRangeFromUserQuery(userQuery, LocalDate.now());
        List<String> currencies = Collections.emptyList();// extractCurrenciesFromUserQuery(userQuery);

        return chatClient.prompt()
                .user(userQuery)
                .advisors(retrievalAugmentationAdvisor)
                .advisors(a -> a.param(VectorStoreDocumentRetriever.FILTER_EXPRESSION, FilterExpressionUtil.fromDateRangeAndCurrencies(dateRange, currencies)))
                .call().content();
    }

    /**
     * extracting currencies from user query using a LLM and then validating them using static crypto codes
     */
    private List<String> extractCurrenciesFromUserQuery(String userQuery) {
        List<String> modelExtracted = modelCurrencyExtractor.extractCurrenciesFromUserQuery(userQuery);
        List<String> refinedCodes = staticCurrencyExtractor.convertToCodes(modelExtracted);
        if (refinedCodes.isEmpty()) {
            return staticCurrencyExtractor.getCurrencyCodes(userQuery);
        }
        return refinedCodes;
    }
}
