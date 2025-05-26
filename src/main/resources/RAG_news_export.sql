#the query to create RAG_news.csv file
SELECT 'id',
       'title',
       'description',
       'newsDatetime',
       'url',
       'positiveVotes',
       'negativeVotes',
       'sourceUrl',
       'currencies'
UNION
SELECT t1.id,
       REPLACE(t1.title, '"', '""'),
       REPLACE(IFNULL(NULLIF(t1.description, '-'), t1.title), '"', '""'),
       t1.newsDatetime,
       t1.url,
       t1.positive,
       t1.negative,
       t1.sourceUrl,
       IFNULL((SELECT GROUP_CONCAT(tt1.code SEPARATOR ',')
               FROM currency tt1
                        JOIN news__currency tt2 ON tt1.id = tt2.currencyId
               WHERE tt2.newsId = t1.id), '')
FROM cryptopanic_news t1
WHERE t1.sourceUrl IS NOT NULL
INTO OUTFILE 'C:/other/projects/CryptopanicAIAssistant/src/main/resources/RAG_news.csv' CHARACTER
SET utf8mb4
    FIELDS TERMINATED BY ',' OPTIONALLY ENCLOSED BY '"' ESCAPED BY ''
    LINES TERMINATED BY '\r\n';
