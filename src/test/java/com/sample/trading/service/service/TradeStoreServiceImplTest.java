package com.sample.trading.service.service;

import com.sample.trading.service.model.TradeStore;
import com.sample.trading.service.repository.TradeStoreRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class TradeStoreServiceImplTest {

    @Autowired
    private TradeStoreService service;

    @MockBean
    private TradeStoreRepository repository;

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    @DisplayName("Test save Success")
    void save() {
        LocalDate maturityDate = LocalDate.now();
        LocalDate now = LocalDate.now();

        TradeStore mockTradeRequest = new TradeStore("T1", 0, "CP-1", maturityDate);
        TradeStore mockTradeResponse = new TradeStore("T1", 1, "CP-1", null, now, now, "N");

        doReturn(mockTradeResponse).when(repository).save(any());

        TradeStore savedTrade = service.save(mockTradeRequest);

        Assertions.assertNotNull(mockTradeResponse, "The saved review should not be null");
        Assertions.assertEquals(1, savedTrade.getVersion().intValue(),"The version for a new trade should be 1");
    }
}