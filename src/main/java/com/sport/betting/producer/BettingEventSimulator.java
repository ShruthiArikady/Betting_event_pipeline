package com.kambi.betting.producer;

import com.kambi.betting.model.BettingEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;
import java.util.Random;
import java.util.UUID;

/**

- Simulates a live sports data feed by publishing random events to Kafka.
- In a real Kambi system this would be replaced by feeds from data providers
- like Sportradar or Betradar.
  */
  @Component
  @ConditionalOnProperty(name ="spring.kafka.enabled", havingValue = "true", matchIfMissing = false)
  public class BettingEventSimulator {
  
  private static final Logger log = LoggerFactory.getLogger(BettingEventSimulator.class);
  
  private final BettingEventProducer producer;
  private final Random random = new Random();
  
  // Simulated ongoing matches
  private static final List<String> MATCH_IDS = List.of(
  "match:arsenal_vs_chelsea",
  "match:lakers_vs_celtics",
  "match:federer_vs_nadal"
  );
  
  private static final List<String> MARKETS  = List.of("MATCH_WINNER", "OVER_UNDER_2_5", "BOTH_TEAMS_SCORE");
  private static final List<String> OUTCOMES = List.of("HOME", "AWAY", "DRAW");
  private static final List<String> CUSTOMERS = List.of("cust:alice", "cust:bob", "cust:carol", "cust:dave");
  
  public BettingEventSimulator(BettingEventProducer producer) {
  this.producer = producer;
  }
  
  /** Publish a random odds update every 3 seconds */
  @Scheduled(fixedDelay = 3000)
  public void simulateOddsUpdate() {
  String matchId = randomFrom(MATCH_IDS);
  double oldOdds = 1.5 + random.nextDouble() * 3.0;
  double newOdds = oldOdds + (random.nextDouble() - 0.5) * 0.4; // slight drift
  newOdds = Math.max(1.01, newOdds); // odds can never go below 1.01
  
  
   var event = new BettingEvent.OddsUpdated(
           UUID.randomUUID().toString(),
           Instant.now(),
           matchId,
           randomFrom(MARKETS),
           randomFrom(OUTCOMES),
           Math.round(oldOdds * 100.0) / 100.0,
           Math.round(newOdds * 100.0) / 100.0
   );
  
   log.info("Simulating OddsUpdated for match={} outcome={} odds={}→{}",
           matchId, event.outcome(), event.oldOdds(), event.newOdds());
   producer.publishOddsUpdated(event);
 
  
  }
  
  /** Publish a random bet placement every 7 seconds */
  @Scheduled(fixedDelay = 7000)
  public void simulateBetPlacement() {
  double odds  = 1.5 + random.nextDouble() * 3.0;
  long   stake = (random.nextInt(20) + 1) * 100L; // £1 to £20 in pence
  
   var event = new BettingEvent.BetPlaced(
           UUID.randomUUID().toString(),
           Instant.now(),
           "bet:" + UUID.randomUUID(),
           randomFrom(CUSTOMERS),
           randomFrom(MATCH_IDS),
           randomFrom(MARKETS),
           randomFrom(OUTCOMES),
           Math.round(odds * 100.0) / 100.0,
           stake
   );
  
   log.info("Simulating BetPlaced customer={} stake={}p odds={}",
           event.customerId(), event.stake(), event.odds());
   producer.publishBetPlaced(event);
  
  
  }
  
  private <T> T randomFrom(List<T> list) {
  return list.get(random.nextInt(list.size()));
  }
  }