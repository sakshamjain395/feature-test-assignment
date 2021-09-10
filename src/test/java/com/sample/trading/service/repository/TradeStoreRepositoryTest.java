package com.sample.trading.service.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.sample.trading.service.model.TradeStore;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@DataMongoTest
class TradeStoreRepositoryTest {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private TradeStoreRepository repository;

    ObjectMapper mapper =
            new ObjectMapper().registerModule(new JavaTimeModule())
                    .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

    private static File SAMPLE_JSON = Paths.get("src", "test", "resources", "data", "test-trade.json").toFile();


    @BeforeEach
    void setUp() throws IOException {
        // Deserialize our JSON file to an array of reviews
        TradeStore[] objects = mapper.readValue(SAMPLE_JSON, TradeStore[].class);
        Arrays.stream(objects).forEach(mongoTemplate::save);
    }

    @AfterEach
    void tearDown() {
        mongoTemplate.dropCollection("Trades");
    }

    @Test
    void testSave() {
        // Create a test Review
        LocalDate maturityDate = LocalDate.now();
        LocalDate now = LocalDate.now();

        TradeStore mockTradeRequest = new TradeStore("T5", 1, "CP-1", maturityDate);


        // Persist the review to MongoDB
        TradeStore savedTrade = repository.save(mockTradeRequest);

        // Retrieve the review
        TradeStore loadedData = repository.findByTradeId(savedTrade.getTradeId());

        // Validations
        Assertions.assertEquals("CP-1", loadedData.getCounterPartyId());
        Assertions.assertEquals(1, loadedData.getVersion().intValue());
    }

    @Test
    void testFindAll() {
        List<TradeStore> reviews = repository.findAll();
        Assertions.assertEquals(4, reviews.size(), "Should be 4 trades in the database");
    }

    @Test
    void testfindByTradeIdSuccess() {
        TradeStore trade = repository.findByTradeId("T3");
        Assertions.assertNotNull(trade);
    }

    @Test
    void testfindByTradeIdNotFound() {
        TradeStore trade = repository.findByTradeId("T5");
        Assertions.assertNull(trade);
    }
}