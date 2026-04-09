package com.kambi.betting.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
@ConditionalOnProperty(
    name = "spring.kafka.enabled",
    havingValue = "true",
    matchIfMissing = false
)
public class KafkaTopicConfig {

    @Value("${betting.kafka.topics.odds-updated}")
    private String oddsUpdatedTopic;

    @Value("${betting.kafka.topics.bet-placed}")
    private String betPlacedTopic;

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