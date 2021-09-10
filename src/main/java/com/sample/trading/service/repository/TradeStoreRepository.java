package com.sample.trading.service.repository;

import com.sample.trading.service.model.TradeStore;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface TradeStoreRepository extends MongoRepository<TradeStore, String> {
    TradeStore findByTradeId(String tradeId);
}
