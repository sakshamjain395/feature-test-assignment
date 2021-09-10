package com.sample.trading.service.service;

import com.sample.trading.service.exception.ResourceConflictException;
import com.sample.trading.service.model.TradeStore;
import com.sample.trading.service.repository.TradeStoreRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class TradeStoreServiceImpl implements TradeStoreService {

    private TradeStoreRepository repository;

    public TradeStoreServiceImpl(TradeStoreRepository repository) {
        this.repository = repository;
    }

    @Override
    public TradeStore save(TradeStore trade) {
        TradeStore savedTrade = null;
        TradeStore response = new TradeStore();

        // 1. if the lower version is being received by the store it will reject the trade and throw an exception
        if (! isTradeVersionValid(trade))
            throw new ResourceConflictException("Trade request rejected.");

        // 2. Store should not allow the trade which has less maturity date then today date.
        // if yes, update the expire flag if in a store the trade crosses the maturity date.

        if(! isTradeMaturityDateValid(trade)){
            TradeStore fetchedTrade = repository.findByTradeId(trade.getTradeId());

            if(fetchedTrade != null){
                BeanUtils.copyProperties(fetchedTrade, trade);
                trade.setExpired("Y");
            }
            else {
                trade.setExpired("Y");
                response.setBookId(null);
            }
        }
        else {
            trade.setExpired("N");
            response.setBookId("B1");
            trade.setVersion(trade.getVersion() + 1);
        }
        trade.setCreatedDate(LocalDate.now());
        savedTrade = repository.save(trade);
        BeanUtils.copyProperties(savedTrade, response);

        return response;
    }

    private boolean isTradeVersionValid(TradeStore trade){
        TradeStore savedTrade = repository.findByTradeId(trade.getTradeId());

        if(savedTrade != null){
            return savedTrade.getVersion() == trade.getVersion();
        }
        else
            return true;
    }

    private boolean isTradeMaturityDateValid(TradeStore trade){
        return trade.getMaturityDate().isAfter(LocalDate.now()) || trade.getMaturityDate().isEqual(LocalDate.now());
    }

}
