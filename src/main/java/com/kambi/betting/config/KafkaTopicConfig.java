package com.kambi.betting.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

/**

- Declares Kafka topics programmatically.
- Spring will auto-create them on startup if they don't exist.
  */
  @SuppressWarnings("null")
  @Configuration
  public class KafkaTopicConfig {
  
  @Value("${betting.kafka.topics.odds-updated}")
  private String oddsUpdatedTopic;
  
  @Value("${betting.kafka.topics.bet-placed}")
  private String betPlacedTopic;
  
  /**
  - 3 partitions → allows 3 parallel consumers in the same consumer group.
  - Good talking point in the interview: partitions = unit of parallelism in Kafka.
    */
    @Bean
    public NewTopic oddsUpdatedTopic() {
    return TopicBuilder.name(oddsUpdatedTopic)
    .partitions(3)
    .replicas(1)
    .build();
    }
  
  @Bean
  public NewTopic betPlacedTopic() {
  return TopicBuilder.name(betPlacedTopic)
  .partitions(3)
  .replicas(1)
  .build();
  }
  }