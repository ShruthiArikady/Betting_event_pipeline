package com.kambi.betting.consumer;

import com.kambi.betting.model.BetDocument;
import com.kambi.betting.model.BettingEvent;
import com.kambi.betting.model.OddsDocument;
import com.kambi.betting.repository.BetRepository;
import com.kambi.betting.repository.BettingEventRepository;
import com.kambi.betting.repository.OddsRepository;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

import com.kambi.betting.Entity.BettingEventEntity;

/**

- Consumes BettingEvents from Kafka and persists them to MongoDB.
- 
- Uses Java 21 pattern matching switch to route events — the sealed interface
- makes this exhaustive: the compiler will error if a new event type is added
- to BettingEvent but not handled here.
  */
  @Service
  public class BettingEventConsumer {
  
  private static final Logger log = LoggerFactory.getLogger(BettingEventConsumer.class);
  
  private final BetRepository   betRepository;
  private final OddsRepository  oddsRepository;
  @Autowired
    private BettingEventRepository repository; 
  
  public BettingEventConsumer(BetRepository betRepository, OddsRepository oddsRepository) {
  this.betRepository  = betRepository;
  this.oddsRepository = oddsRepository;
  }
  
  /**
  - Single listener that handles both topics.
  - groupId ties this consumer to a consumer group — Kafka will distribute
  - partitions across all instances in the same group automatically.
    */
    @KafkaListener(
    topics  = {"${betting.kafka.topics.odds-updated}", "${betting.kafka.topics.bet-placed}"},groupId = "${spring.kafka.consumer.group-id}"
    )
    public void consume(ConsumerRecord<String, BettingEvent> record) {
    BettingEvent event = record.value();
    log.debug("Received event {} from topic={} partition={} offset={}",event.eventId(), record.topic(), record.partition(), record.offset());
    
    // Java 21 pattern matching switch — exhaustive over the sealed interface
    switch (event) {
    case BettingEvent.OddsUpdated oddsUpdated -> handleOddsUpdated(oddsUpdated);
    case BettingEvent.BetPlaced   betPlaced   -> handleBetPlaced(betPlaced);
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
  event.newOdds() - event.oldOdds(),   // movement
  event.occurredAt(),
  Instant.now()
  );
  oddsRepository.save(doc);
  log.info("Saved OddsUpdate matchId={} market={} {}→{}",
  event.matchId(), event.market(), event.oldOdds(), event.newOdds());
  }
  
  private void handleBetPlaced(BettingEvent.BetPlaced event) {
  long potentialPayout = Math.round(event.stake() * event.odds());
  
   var doc = new BetDocument(
           UUID.randomUUID().toString(),
           event.betId(),
           event.customerId(),
           event.matchId(),
           event.outcome(),
           event.odds(),
           event.stake(),
           potentialPayout,
           "ACCEPTED",
           event.occurredAt(),
           Instant.now()
   );
   betRepository.save(doc);
   log.info("Saved Bet betId={} customer={} stake={}p payout={}p",
           event.betId(), event.customerId(), event.stake(), potentialPayout);

  
  }
  
 @KafkaListener(topics = "${betting.kafka.topics.bet-placed}")
    public void consumeBetPlaced(String message) {
        BettingEventEntity event = new BettingEventEntity();
        event.setEventType("BET_PLACED");
        event.setTimestamp(LocalDateTime.now());
        // parse message fields...
        repository.save(event);
    }


  }