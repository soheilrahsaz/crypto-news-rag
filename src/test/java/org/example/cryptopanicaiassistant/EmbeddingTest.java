package org.example.cryptopanicaiassistant;

import org.junit.jupiter.api.Test;
import org.springframework.ai.transformers.TransformersEmbeddingModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;

@SpringBootTest
class EmbeddingTest {

    @Autowired
    TransformersEmbeddingModel transformersEmbeddingModel;

    @Test
    void testCreateEmbeddings() {
        float[] embedded = transformersEmbeddingModel.embed("The sky is blue");
        System.out.println(Arrays.toString(embedded));
    }
}
