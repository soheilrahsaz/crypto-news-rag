package org.example.cryptopanicaiassistant.vectorstore;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.type.DataTypes;
import io.micrometer.observation.ObservationRegistry;
import org.example.cryptopanicaiassistant.embeddings.NormilizingEmbeddingModel;
import org.springframework.ai.embedding.BatchingStrategy;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.cassandra.CassandraVectorStore;
import org.springframework.ai.vectorstore.observation.VectorStoreObservationConvention;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class CassandraConfiguration {


    @Bean("vectorStore")
    public CassandraVectorStore simpleCassandraVectorStore(EmbeddingModel embeddingModel,
                                                           CqlSession cqlSession, ObjectProvider<ObservationRegistry> observationRegistry,
                                                           ObjectProvider<VectorStoreObservationConvention> customObservationConvention,
                                                           BatchingStrategy batchingStrategy) {

        return CassandraVectorStore.builder(new NormilizingEmbeddingModel(embeddingModel))
                .session(cqlSession)
                .keyspace("crypto_ai_assist")
                .table("crypto_news")
                .indexName("crypto_news_embedding_vector_idx")
                .contentColumnName("description")
                .embeddingColumnName("embedding")
                .partitionKeys(List.of(new CassandraVectorStore.SchemaColumn("id", DataTypes.TEXT)))

                .addMetadataColumn(new CassandraVectorStore.SchemaColumn("title", DataTypes.TEXT))
                .addMetadataColumn(new CassandraVectorStore.SchemaColumn("newsdatetime", DataTypes.TIMESTAMP))
                .addMetadataColumn(new CassandraVectorStore.SchemaColumn("votescore", DataTypes.FLOAT))
                .addMetadataColumn(new CassandraVectorStore.SchemaColumn("urls", DataTypes.frozenListOf(DataTypes.TEXT)))
                .addMetadataColumn(new CassandraVectorStore.SchemaColumn("currencies", DataTypes.listOf(DataTypes.TEXT)))

                .fixedThreadPoolExecutorSize(15)
                .initializeSchema(false)
                .observationRegistry(observationRegistry.getIfUnique(() -> ObservationRegistry.NOOP))
                .customObservationConvention(customObservationConvention.getIfAvailable(() -> null))
                .batchingStrategy(batchingStrategy).build();
    }
}
