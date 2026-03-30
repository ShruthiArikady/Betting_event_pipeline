package com.kambi.betting.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;

@Entity
@Table(name = "odds")
public record OddsDocument(
    @Id String id,
    String matchId,
    String market,
    String outcome,
    double oldOdds,
    double newOdds,
    double oddsChange,
    Instant occurredAt,
    Instant savedAt
) {}