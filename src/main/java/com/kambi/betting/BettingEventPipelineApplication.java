package com.kambi.betting;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**

- Entry point for the Betting Event Pipeline.
- 
- Virtual threads are enabled via application.yml:
- spring.threads.virtual.enabled=true
- This means every incoming HTTP request and Kafka consumer callback
- runs on a lightweight virtual thread (Java 21) instead of a platform
- thread — much better throughput under I/O-heavy workloads like this one.
  */
  @SpringBootApplication
  @EnableScheduling
  public class BettingEventPipelineApplication {
  
  public static void main(String[] args) {
  SpringApplication.run(BettingEventPipelineApplication.class, args);
  }
  }