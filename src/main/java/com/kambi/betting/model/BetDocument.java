package com.kambi.betting.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.Indexed;

import java.time.Instant;

/**

- MongoDB document storing a processed bet.
  */
  @Document(collection = "bets")
  public record BetDocument(
  @Id String id,
  @Indexed String betId,
  @Indexed String customerId,
  String matchId,
  String outcome,
  double odds,
  long stake,
  long potentialPayout,   // stake * odds (in minor units)
  String status,          // ACCEPTED, REJECTED
  Instant placedAt,
  Instant processedAt
  ) {}