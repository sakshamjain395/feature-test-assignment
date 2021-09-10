package com.sample.trading.service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.sample.trading.service.model.TradeStore;
import com.sample.trading.service.repository.TradeStoreRepository;
import com.sample.trading.service.service.TradeStoreService;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.TimeZone;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
class TradeStoreControllerTest {

    @MockBean
    private TradeStoreService service;

    @MockBean
    private TradeStoreRepository repository;

    @Autowired
    private MockMvc mockMvc;

    private static DateFormat df = new SimpleDateFormat("yyyy-MM-dd");

    @BeforeAll
    static void beforeAll() {
        df.setTimeZone(TimeZone.getTimeZone("GMT"));
    }

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    @DisplayName("POST /trade - Should return Success with valid input. Http_Status_Code = 201")
    void createTrade() throws Exception {
        TradeStore mockTradeResponse = new TradeStore("T1", 1, "CP-1", "B1", LocalDate.now(), LocalDate.now(), "N");

        doReturn(mockTradeResponse).when(service).save(any());

        mockMvc.perform(post("/trade")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(mockTradeResponse)))

                // Validate the response code and content type
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))

                // Validate the headers
                .andExpect(header().string(HttpHeaders.ETAG, "\"1\""))
                .andExpect(header().string(HttpHeaders.LOCATION, "/trade/B1"))

                // Validate the returned fields
                .andExpect(jsonPath("$.tradeId", is("T1")))
                .andExpect(jsonPath("$.version", is(1)))
                .andExpect(jsonPath("$.counterPartyId", is("CP-1")))
                .andExpect(jsonPath("$.bookId", is("B1")))
                .andExpect(jsonPath("$.expired", is("N")));
    }

    @Test
    @DisplayName("POST /trade - Should update Expired flag Y if maturity date is less than today Version Mismatch.")
    void createTradeShouldUpdateExpiredFlagYes() throws Exception {
        LocalDate pastMaturityDate = LocalDate.now().minusDays(1);

        TradeStore mockTradeRequest = new TradeStore("T1", 0, "CP-1", pastMaturityDate);
        TradeStore mockTradeResponse = new TradeStore("T1", 1, "CP-1", null, pastMaturityDate, LocalDate.now(), "Y");

        doReturn(mockTradeResponse).when(service).save(any());

        mockMvc.perform(post("/trade")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(mockTradeResponse)))

                // Validate the response code and content type
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))

                // Validate the returned fields
                .andExpect(jsonPath("$.expired", is("Y")));
    }


//    @DisplayName("POST /trades - Should return Conflict For Version Mismatch. Http_Status_Code = 409")
//    void createTradeShouldReturnConflictForVersionMismatch() throws Exception {
//        TradeStoreRequest mockTradeRequest = new TradeStoreRequest("T1", 0, "CP-1", LocalDate.now());
//        TradeStoreResponse mockTradeResponse = new TradeStoreResponse("T1", 1, "CP-1", "B1", LocalDate.now(), LocalDate.now(), "N");
//
//        doReturn(mockTradeResponse).when(repository).findByTradeId(mockTradeRequest.getTradeId());
//
//        doThrow(new ResourceConflictException()).when(service).save(mockTradeRequest);
//
//        mockMvc.perform(post("/trade")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(asJsonString(mockTradeResponse)))
//
//                // Validate the response code and content type
//                .andExpect(status().isConflict())
//                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
//    }

    static String asJsonString(final Object obj) {
        try {
            ObjectMapper objectMapper =
                    new ObjectMapper().registerModule(new JavaTimeModule())
                            .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

            return objectMapper.writeValueAsString(obj);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}