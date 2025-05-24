package org.example.cryptopanicaiassistant.embeddings;

import org.example.cryptopanicaiassistant.newssource.RawNews;
import org.springframework.ai.document.Document;

import java.util.List;

public interface DocumentMapper {
    List<Document> from(RawNews rawNews);
}
