package com.kambi.betting.producer;

import com.kambi.betting.model.BettingEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

/**

- Publishes BettingEvents to Kafka topics.
- 
- Key design decisions (good to mention in the interview):
- - Uses matchId as the Kafka message key → guarantees all events for the
- same match go to the same partition → preserves ordering per match.
- - Async send with CompletableFuture → does not block the caller thread.
    */
   @Service
    @ConditionalOnProperty(name="spring.kafka.enabled",havingValue = "true", matchIfMissing = false)
    public class BettingEventProducer {
  
  private static final Logger log = LoggerFactory.getLogger(BettingEventProducer.class);
  
  private final KafkaTemplate<String, BettingEvent> kafkaTemplate;
  
  @Value("${betting.kafka.topics.odds-updated}")
  private String oddsUpdatedTopic;
  
  @Value("${betting.kafka.topics.bet-placed}")
  private String betPlacedTopic;
  
  public BettingEventProducer(KafkaTemplate<String, BettingEvent> kafkaTemplate) {
  this.kafkaTemplate = kafkaTemplate;
  }
  
  public void publishOddsUpdated(BettingEvent.OddsUpdated event) {
  publish(oddsUpdatedTopic, event.matchId(), event);
  }
  
  public void publishBetPlaced(BettingEvent.BetPlaced event) {
  // Key by customerId so bets from the same customer stay ordered
  publish(betPlacedTopic, event.customerId(), event);
  }
  
  private void publish(String topic, String key, BettingEvent event) {
  CompletableFuture<SendResult<String, BettingEvent>> future =
  kafkaTemplate.send(topic, key, event);
  
   future.whenComplete((result, ex) -> {
       if (ex != null) {
           log.error("Failed to publish event {} to topic {}: {}",
                   event.eventId(), topic, ex.getMessage());
       } else {
           log.debug("Published event {} to topic={} partition={} offset={}",
                   event.eventId(),
                   result.getRecordMetadata().topic(),
                   result.getRecordMetadata().partition(),
                   result.getRecordMetadata().offset());
       }
   });
  
  
  }
  }