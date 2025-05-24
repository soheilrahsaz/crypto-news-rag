package org.example.cryptopanicaiassistant.newssource.csv;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.example.cryptopanicaiassistant.newssource.RawNews;
import org.example.cryptopanicaiassistant.newssource.RawNewsRepository;
import org.springframework.core.io.ClassPathResource;

import java.io.FileReader;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

public class RawNewsCSVRepositoryImpl implements RawNewsRepository {
    private final CSVFormat csvFormat = CSVFormat.DEFAULT.builder()
            .setHeader()
            .setSkipHeaderRecord(true)
            .setQuote(Character.valueOf('"'))
            .setDelimiter(",")
            .get();

    private FileReader getFileReader() throws IOException {
        return new FileReader(new ClassPathResource("/RAG_news.csv").getFile());
    }


    private RawNews toRawNews(CSVRecord csvRecord) {
        return RawNews.builder()
                .id(Integer.valueOf(csvRecord.get("id")))
                .title(csvRecord.get("title"))
                .description(csvRecord.get("description"))
                .newsDatetime(Timestamp.valueOf(csvRecord.get("newsDatetime")))
                .positiveVotes(Integer.valueOf(csvRecord.get("positiveVotes")))
                .negativeVotes(Integer.valueOf(csvRecord.get("negativeVotes")))
                .url(csvRecord.get("url"))
                .sourceUrl(csvRecord.get("sourceUrl"))
                .currencies(Arrays.stream(csvRecord.get("currencies").split(",")).filter(code -> !code.isBlank()).toList())
                .build();
    }

    private <T> T withParsedCsv(Function<Stream<RawNews>, T> handler) {
        try (CSVParser csvParser = csvFormat.parse(getFileReader())) {
            return handler.apply(csvParser.stream().map(this::toRawNews));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<RawNews> findAll() {
        return withParsedCsv(Stream::toList);
    }

    @Override
    public List<RawNews> findAll(Long limit) {
        return withParsedCsv(stream -> stream.limit(limit).toList());
    }

    @Override
    public RawNews findById(int id) {
        return withParsedCsv(stream -> stream.filter(rawNews -> rawNews.getId() == id).findFirst().orElse(null));
    }
}
