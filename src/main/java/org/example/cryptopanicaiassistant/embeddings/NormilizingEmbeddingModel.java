package org.example.cryptopanicaiassistant.embeddings;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.Embedding;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.embedding.EmbeddingRequest;
import org.springframework.ai.embedding.EmbeddingResponse;

/**
 * decorator class which will normalize the vector output of the embedding model
 */
@RequiredArgsConstructor
public class NormilizingEmbeddingModel implements EmbeddingModel {
    private final EmbeddingModel embeddingModel;

    @Override
    public @NonNull EmbeddingResponse call(@NonNull EmbeddingRequest request) {
        EmbeddingResponse embeddingResponse = embeddingModel.call(request);
        return new EmbeddingResponse(embeddingResponse.getResults().stream().map(e -> new Embedding(l2Normalize(e.getOutput()), e.getIndex(), e.getMetadata())).toList(), embeddingResponse.getMetadata());
    }

    @Override
    public float @NonNull [] embed(@NonNull Document document) {
        return embeddingModel.embed(document);
    }

    private float vectorNorm(float[] vector) {
        double sum = 0;
        for (float v : vector) {
            sum += v * v;
        }
        return (float)Math.sqrt(sum);
    }

    private float[] l2Normalize(float[] vector) {
        float norm = vectorNorm(vector);

        float[] normalized = new float[vector.length];
        for (int i = 0; i < vector.length; i++) {
            normalized[i] = (vector[i] / norm);
        }
        return normalized;
    }
}
