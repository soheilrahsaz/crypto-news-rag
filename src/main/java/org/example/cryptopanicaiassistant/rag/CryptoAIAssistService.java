package org.example.cryptopanicaiassistant.rag;


import lombok.NonNull;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.document.Document;
import org.springframework.ai.rag.advisor.RetrievalAugmentationAdvisor;
import org.springframework.ai.rag.generation.augmentation.ContextualQueryAugmenter;
import org.springframework.ai.rag.retrieval.search.VectorStoreDocumentRetriever;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.filter.Filter;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;


@Service
public class CryptoAIAssistService {
    private final RetrievalAugmentationAdvisor retrievalAugmentationAdvisor;
    private final ChatClient chatClient;
    private final ModelDateRangeExtractor modelDateRangeExtractor;
    private final ModelCurrencyExtractor modelCurrencyExtractor;
    private final StaticCurrencyExtractor staticCurrencyExtractor;
    private static final String PROMPT_TEMPLATE = """
			Context information is below.

			---------------------
			{context}
			---------------------
			Each news item includes a user sentiment score from -1 (strongly negative) to +1 (strongly positive), based on real user votes.

			Given the context information and no prior knowledge, answer the query.

			Instructions:
			1. If the answer is not in the context, just say that you don't know.
			2. Avoid statements like "Based on the context..." or "The provided information...".
			3. At the end, list the URLs of the articles you relied on when forming your response. Ignore unrelated articles.

			Query: {query}

			Answer:
			""";

    public CryptoAIAssistService(VectorStore vectorStore, ChatModel chatModel) {
        this.retrievalAugmentationAdvisor = RetrievalAugmentationAdvisor.builder()
                .documentRetriever(VectorStoreDocumentRetriever.builder()
                        .vectorStore(vectorStore)
                        .topK(5)
                        .similarityThreshold(0.7d)
                        .build())
                .queryAugmenter(ContextualQueryAugmenter.builder()
                        .documentFormatter(this::documentFormatter)
                        .promptTemplate(new PromptTemplate(PROMPT_TEMPLATE))
                        .build())
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

    private String documentFormatter(List<Document> documents){
        return documents.stream().map(document -> new StringBuilder()
                        .append("Title: ").append(document.getMetadata().get("title")).append("\n")
                        .append("Description: ").append(document.getText()).append("\n")
                        .append("Url: ").append(document.getMetadata().get("urls")).append("\n")
                        .append("Datetime: ").append(document.getMetadata().get("newsdatetime")).append("\n")
                        .append("User Sentiment Score: ").append(String.format("%.2f", (float)document.getMetadata().get("votescore"))).append("\n")
                        )
                .collect(Collectors.joining("\n\n"));
    }

    public String ask(String userQuery) {
        ModelDateRangeExtractor.DateRange dateRange = modelDateRangeExtractor.extractDateRangeFromUserQuery(userQuery, LocalDate.now());
        List<String> currencies = Collections.emptyList();// extractCurrenciesFromUserQuery(userQuery);

        return chatClient.prompt()
                .user(userQuery)
                .advisors(retrievalAugmentationAdvisor)
                .advisors(a -> a.param(VectorStoreDocumentRetriever.FILTER_EXPRESSION, createFilter(dateRange, currencies)))
                .call().content();
    }

    private Filter.Expression createFilter(ModelDateRangeExtractor.DateRange dateRange, @NonNull List<String> currencies) {
        Filter.Expression dateRangeFilterExpression = null;
        if (dateRange != null) {
            Filter.Expression datetimeGreaterThanExpression = new Filter.Expression(Filter.ExpressionType.GTE, new Filter.Key("newsdatetime"), new Filter.Value(dateRange.startDate().atStartOfDay(ZoneOffset.UTC).toInstant()));
            Filter.Expression datetimeLessThanExpression = new Filter.Expression(Filter.ExpressionType.LTE, new Filter.Key("newsdatetime"), new Filter.Value(dateRange.endDate().atStartOfDay(ZoneOffset.UTC).toInstant()));
            dateRangeFilterExpression = new Filter.Expression(Filter.ExpressionType.AND, datetimeGreaterThanExpression, datetimeLessThanExpression);
        }

        if (currencies.isEmpty()) {
            return dateRangeFilterExpression;
        }
        Filter.Expression currenciesFilterExpression = new Filter.Expression(Filter.ExpressionType.IN, new Filter.Key("currencies"), new Filter.Value(currencies));

        if(dateRange == null){
            return currenciesFilterExpression;
        }

        return new Filter.Expression(Filter.ExpressionType.AND, dateRangeFilterExpression, currenciesFilterExpression);
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
