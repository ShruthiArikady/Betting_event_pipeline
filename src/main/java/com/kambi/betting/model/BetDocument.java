package com.kambi.betting.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;

@Entity
@Table(name = "bets")
public record BetDocument(
    @Id String id,
    String betId,
    String customerId,
    String matchId,
    String market,
    String outcome,
    double odds,
    double stake,
    double potentialPayout,
    String status,
    Instant placedAt
) {}