package com.kambi.betting.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.Indexed;

import java.time.Instant;

/**

- MongoDB document storing an odds update snapshot.
  */
  @Document(collection = "odds")
  public record OddsDocument(
  @Id String id,
  @Indexed String matchId,
  String market,
  String outcome,
  double oldOdds,
  double newOdds,
  double movement,        // newOdds - oldOdds: positive = odds drifted, negative = odds shortened
  Instant updatedAt,
  Instant processedAt
  ) {}