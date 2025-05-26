DROP TABLE IF EXISTS crypto_news;
CREATE TABLE crypto_news
(
    id           TEXT PRIMARY KEY,
    title        TEXT,
    description  TEXT,
    newsdatetime TIMESTAMP,
    urls         FROZEN<LIST<TEXT>>,
    votescore    FLOAT,
    currencies   LIST<TEXT>,
    embedding    VECTOR<FLOAT, 384>
);

CREATE CUSTOM INDEX crypto_news_embedding_vector_idx
    ON crypto_news (embedding)
    USING 'StorageAttachedIndex'
            WITH OPTIONS = {'similarity_function': 'DOT_PRODUCT'};

CREATE CUSTOM INDEX crypto_news_newsDatetime_idx
    ON crypto_news (newsdatetime)
    USING 'StorageAttachedIndex';

CREATE CUSTOM INDEX crypto_news_currencies_idx
    ON crypto_news (currencies)
    USING 'StorageAttachedIndex';
