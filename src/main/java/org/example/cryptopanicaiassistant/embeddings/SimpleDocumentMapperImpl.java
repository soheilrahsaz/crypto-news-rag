package org.example.cryptopanicaiassistant.embeddings;

import org.example.cryptopanicaiassistant.newssource.RawNews;
import org.springframework.ai.document.Document;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

@Component
public class SimpleDocumentMapperImpl implements DocumentMapper {
    @Override
    public List<Document> from(RawNews rawNews) {
        return List.of(new Document(rawNews.getId() + "", rawNews.getDescription(),
                Map.of("title", rawNews.getTitle(),
                        "newsDatetime", rawNews.getNewsDatetime().toInstant(),
                        "votescore", rawNews.computeWeightedScore(1),
                        "urls", Stream.of(rawNews.getUrl(), rawNews.getSourceUrl()).filter(Objects::nonNull).toList(),
                        "currencies", rawNews.getCurrencies()
                )));
    }
}
