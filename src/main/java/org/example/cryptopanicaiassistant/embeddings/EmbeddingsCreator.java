package org.example.cryptopanicaiassistant.embeddings;

import lombok.RequiredArgsConstructor;
import org.example.cryptopanicaiassistant.newssource.RawNews;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class EmbeddingsCreator {
    private final VectorStore vectorStore;
    private final DocumentMapper documentMapper;

    public void createAndSaveEmbeddings(List<RawNews> rawNews){
        vectorStore.accept(rawNews.stream().flatMap(r -> documentMapper.from(r).stream()).toList());
    }
}
