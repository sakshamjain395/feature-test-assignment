package com.sample.trading.service.controller;

import com.sample.trading.service.model.TradeStore;
import com.sample.trading.service.service.TradeStoreService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.net.URISyntaxException;

@RestController
public class TradeStoreController {
    private static final Logger logger = LogManager.getLogger(TradeStoreController.class);

    @Autowired
    private TradeStoreService service;

    public TradeStoreController(TradeStoreService tradeStoreService) {
        this.service = service;
    }

    @PostMapping("/trade")
    public ResponseEntity<?> createTrade(@RequestBody TradeStore trade) {
        logger.info("Creating new trade for book-id: {}, {}", trade.getTradeId(), trade);


        // Save the new trade to the database
        TradeStore newTrade = service.save(trade);
        logger.info("Trade saved with details: {}", newTrade);

        try {
            // Build a created response
            return ResponseEntity
                    .created(new URI("/trade/" + newTrade.getBookId()))
                    .eTag(Integer.toString(newTrade.getVersion()))
                    .body(newTrade);
        } catch (URISyntaxException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
