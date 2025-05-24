package org.example.cryptopanicaiassistant;

import org.junit.jupiter.api.Test;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.filter.Filter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.sql.Timestamp;
import java.util.List;

@SpringBootTest
class VectorSearchTest {

    @Autowired
    VectorStore vectorStore;


    @Test
    void testSearchSimple2() {
        Filter.Expression datetimeGreaterThanExpression = new Filter.Expression(Filter.ExpressionType.GTE, new Filter.Key("newsdatetime"), new Filter.Value(Timestamp.valueOf("2024-03-03 00:00:00").toInstant()));
        Filter.Expression datetimeLessThanExpression = new Filter.Expression(Filter.ExpressionType.LTE, new Filter.Key("newsdatetime"), new Filter.Value(Timestamp.valueOf("2025-03-03 00:00:00").toInstant()));
        Filter.Group datetimeRangeExpression = new Filter.Group(new Filter.Expression(Filter.ExpressionType.AND, datetimeGreaterThanExpression, datetimeLessThanExpression));
        Filter.Expression containsCurrencyExpression = new Filter.Expression(Filter.ExpressionType.IN, new Filter.Key("currencies"), new Filter.Value(List.of(List.of("BTC"))));

        List<Document> documents = vectorStore.similaritySearch(SearchRequest.builder()
                .query("Trump's affect on crypto currency")
//                .filterExpression(new Filter.Expression(Filter.ExpressionType.AND, containsCurrencyExpression, datetimeRangeExpression))
                .filterExpression(containsCurrencyExpression)
                .topK(5)
                .build());
        for (Document document : documents) {
            System.out.println(document.getScore() + ": " + document.getText());
        }
    }

    @Test
    void testSearchSimple() {
        List<Document> documents = vectorStore.similaritySearch(SearchRequest.builder()
                .query("What is going on with bitcoin")
                .topK(5)
                .build());
        for (Document document : documents) {
            System.out.println(document);
        }
    }
}
