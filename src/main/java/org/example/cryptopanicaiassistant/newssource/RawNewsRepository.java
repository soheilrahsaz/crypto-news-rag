package org.example.cryptopanicaiassistant.newssource;

import java.util.List;

public interface RawNewsRepository {
    List<RawNews> findAll();

    List<RawNews> findAll(Long limit);

    RawNews findById(int id);
}
