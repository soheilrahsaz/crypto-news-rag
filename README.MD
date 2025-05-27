## Crypto AI Assist RAG with Cassandra
This repository contains a Retrieval-Augmented Generation (RAG) application built to showcase the integration of **Spring AI** and **Apache Cassandra**'s Vector Search for querying cryptocurrency news using natural language. 
The application retrieves relevant news articles based on user queries, leveraging embeddings for semantic search and Cassandra for scalable vector storage.
For a detailed explanation of the project, including setup, architecture, and implementation, refer to the [Medium article](https://medium.com/@soheilrahsaz.sr/building-a-rag-application-with-crypto-news-apache-cassandra-and-spring-ai-6115ad5ca75e).

### Dataset Source
This project uses real-world cryptocurrency news articles from https://github.com/soheilrahsaz/cryptoNewsDataset. 
The dataset includes user reactions such as likes, dislikes, and sentiment votes, which are used for reranking search results.

### Features
* Stores and queries embeddings in Apache Cassandra with vector search.
* Natural language query processing for crypto news.
* Extracts date ranges and cryptocurrencies from queries using LLMs.
* Uses `all-MiniLM-L6-v2` model for generating embeddings.
* Reranks results based on user votes for relevance.
* Built with Cassandra, Spring AI, Ollama (Llama 3.2).
