package org.example.cryptopanicaiassistant.embeddings;

import lombok.RequiredArgsConstructor;
import org.example.cryptopanicaiassistant.newssource.RawNews;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
public class NewsEmbeddingSaver {
    private final VectorStore vectorStore;

    public void createAndSaveEmbeddings(List<RawNews> rawNews){
        List<Document> documents = rawNews.stream().map(this::toDocument).toList();
        vectorStore.accept(documents);
    }

    private Document toDocument(RawNews rawNews) {
        return new Document(rawNews.getId() + "", rawNews.getDescription(),
                Map.of("title", rawNews.getTitle(),
                        "newsdatetime", rawNews.getNewsDatetime().toInstant(),
                        "votescore", rawNews.computeWeightedScore(1),
                        "urls", Stream.of(rawNews.getUrl(), rawNews.getSourceUrl()).filter(Objects::nonNull).toList(),
                        "currencies", rawNews.getCurrencies()
                ));
    }
}
