package com.kambi.betting.consumer;

import com.kambi.betting.model.BetDocument;
import com.kambi.betting.model.BettingEvent;
import com.kambi.betting.model.OddsDocument;
import com.kambi.betting.repository.BetRepository;
import com.kambi.betting.repository.OddsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import java.time.Instant;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(
    name = "spring.kafka.enabled",
    havingValue = "true",
    matchIfMissing = false
)
public class BettingEventConsumer {

    private final OddsRepository oddsRepository;
    private final BetRepository betRepository;

    @KafkaListener(
        topics = {"${betting.kafka.topics.odds-updated}",
                  "${betting.kafka.topics.bet-placed}"},
        groupId = "${spring.kafka.consumer.group-id}"
    )
    public void consume(ConsumerRecord<String, BettingEvent> record) {
        BettingEvent event = record.value();
        log.debug("Received event {} from topic={} partition={} offset={}",
            event.eventId(), record.topic(),
            record.partition(), record.offset());

        switch (event) {
            case BettingEvent.OddsUpdated oddsUpdated ->
                handleOddsUpdated(oddsUpdated);
            case BettingEvent.BetPlaced betPlaced ->
                handleBetPlaced(betPlaced);
        }
    }

    private void handleOddsUpdated(BettingEvent.OddsUpdated event) {
        var doc = new OddsDocument(
            UUID.randomUUID().toString(),
            event.matchId(),
            event.market(),
            event.outcome(),
            event.oldOdds(),
            event.newOdds(),
            event.newOdds() - event.oldOdds(),
            event.occurredAt(),
            Instant.now()
        );
        oddsRepository.save(doc);
        log.info("Saved OddsUpdate matchId={} market={} {}->{}",
            event.matchId(), event.market(),
            event.oldOdds(), event.newOdds());
    }

    private void handleBetPlaced(BettingEvent.BetPlaced event) {
        long potentialPayout = Math.round(event.stake() * event.odds());
        var doc = new BetDocument(
            UUID.randomUUID().toString(),
            event.betId(),
            event.customerId(),
            event.matchId(),
            event.market(),
            event.outcome(),
            event.odds(),
            event.stake(),
            potentialPayout,
            "PLACED",
            Instant.now()
        );
        betRepository.save(doc);
    }
}