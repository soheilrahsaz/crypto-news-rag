package org.example.cryptopanicaiassistant.rag;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.document.Document;
import org.springframework.ai.rag.Query;
import org.springframework.ai.rag.postretrieval.document.DocumentPostProcessor;

import java.util.Comparator;
import java.util.List;

/**
 * sorts news based on the absolute value of the `votescore` and selects top elements
 */
@RequiredArgsConstructor
public class VoteScoreDocumentRanker implements DocumentPostProcessor {
    private final int limit;
    @Override
    public List<Document> process(Query query, List<Document> documents) {
        return documents.stream().sorted(Comparator.comparing(this::getAbsVoteScore).reversed()).limit(limit).toList();
    }

    private float getAbsVoteScore(Document doc) {
        return Math.abs((float) doc.getMetadata().get("votescore"));
    }
}
