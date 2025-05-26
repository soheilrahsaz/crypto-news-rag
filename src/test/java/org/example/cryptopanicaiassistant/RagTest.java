package org.example.cryptopanicaiassistant;

import org.example.cryptopanicaiassistant.rag.CryptoAIAssistService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class RagTest {

    @Autowired
    CryptoAIAssistService cryptoAIAssistService;

    @Test
    void testSimpleRag() {
        System.out.println(cryptoAIAssistService.ask("Trumps effect on bitcoin since january 2025"));
    }
}
