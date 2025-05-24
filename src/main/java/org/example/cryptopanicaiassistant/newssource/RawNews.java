package org.example.cryptopanicaiassistant.newssource;

import lombok.*;

import java.sql.Timestamp;
import java.util.List;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RawNews {
    private Integer id;
    private String title;
    private String description;
    private Timestamp newsDatetime;
    private String url;
    private Integer positiveVotes;
    private Integer negativeVotes;
    private String sourceUrl;
    private List<String> currencies;

    public float computeWeightedScore(int k) {
        int totalVotes = positiveVotes + negativeVotes;

        if (totalVotes == 0) return 0;

        double rawScore = (double)(positiveVotes - negativeVotes) / totalVotes;

        double confidence = 1 - Math.exp(-((double) totalVotes / k));

        return (float)(rawScore * confidence);
    }

    public float computeScore() {
        int totalVotes = positiveVotes + negativeVotes;

        if (totalVotes == 0) return 0;

        return (float)(positiveVotes - negativeVotes) / totalVotes;
    }
}
