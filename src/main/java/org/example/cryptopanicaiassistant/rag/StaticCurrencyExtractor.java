package org.example.cryptopanicaiassistant.rag;

import lombok.NonNull;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.nio.file.Files;
import java.util.*;


public class StaticCurrencyExtractor{
    private final Set<String> codes;
    private final Map<String, String> nameCodeMap;

    public StaticCurrencyExtractor() {
        try {
            Set<String> codes = new HashSet<>();
            Map<String, String> nameCodeMap = new HashMap<>();
            List<String> lines = Files.readAllLines(new ClassPathResource("/known_cryptos.txt").getFile().toPath());

            for (String line : lines) {
                String[] parts = line.split(":");
                codes.add(parts[0].trim().toLowerCase());
                nameCodeMap.put(parts[1].trim().toLowerCase(), parts[0].trim().toLowerCase());
            }

            this.codes = Collections.unmodifiableSet(codes);
            this.nameCodeMap = Collections.unmodifiableMap(nameCodeMap);
        } catch (IOException e) {
            throw new RuntimeException("unable to get cryptos list", e);
        }
    }

    /**
     * tries to find exact crypto codes and names from user raw query
     */
    public @NonNull List<String> getCurrencyCodes(@NonNull String userRawQuery) {
        Set<String> extracedCodes = new HashSet<>();
        for (String word : userRawQuery.split("\\s|\\.|,")) {
            word = word.toLowerCase();

            if(codes.contains(word)) {
                extracedCodes.add(word);
            }
            if(nameCodeMap.containsKey(word)) {
                extracedCodes.add(nameCodeMap.get(word));
            }
        }

        return new ArrayList<>(extracedCodes);
    }

    /**
     * from a list containing for codes and names, returns only codes and dismisses invalid values
     */
    public @NonNull List<String> convertToCodes(@NonNull List<String> currencies) {
        Set<String> extracedCodes = new HashSet<>();
        for (String word : currencies) {
            word = word.toLowerCase();

            if(codes.contains(word)) {
                extracedCodes.add(word);
            }
            if(nameCodeMap.containsKey(word)) {
                extracedCodes.add(nameCodeMap.get(word));
            }
        }
        return new ArrayList<>(extracedCodes);
    }
}
