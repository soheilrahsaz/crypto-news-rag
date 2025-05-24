package org.example.cryptopanicaiassistant;

import org.example.cryptopanicaiassistant.embeddings.EmbeddingsCreator;
import org.example.cryptopanicaiassistant.newssource.mysql.RawNewsMySQLRepositoryImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
class FillVectorDB {
    @Autowired
    EmbeddingsCreator embeddingsCreator;

    @Autowired
    RawNewsMySQLRepositoryImpl newsRepository;

    @Test
    void readAndStoreAll() {
        embeddingsCreator.createAndSaveEmbeddings(newsRepository.findAll());
    }

    @Test
    void testStoreSingle() {
        embeddingsCreator.createAndSaveEmbeddings(List.of(newsRepository.findById(882)));
    }

}
