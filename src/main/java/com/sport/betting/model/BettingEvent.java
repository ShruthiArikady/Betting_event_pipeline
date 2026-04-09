package com.kambi.betting.model;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.time.Instant;

/**

- Sealed interface representing all possible betting domain events.
- Uses Java 21 sealed interfaces to enforce exhaustive pattern matching
- in the consumer — the compiler guarantees all cases are handled.
  */
  @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
  @JsonSubTypes({
  @JsonSubTypes.Type(value = BettingEvent.OddsUpdated.class, name = "ODDS_UPDATED"),
  @JsonSubTypes.Type(value = BettingEvent.BetPlaced.class,   name = "BET_PLACED")
  })
  public sealed interface BettingEvent permits BettingEvent.OddsUpdated, BettingEvent.BetPlaced {
  
  String eventId();
  Instant occurredAt();
  
  /**
  - Fired when a bookmaker updates the odds for a market outcome.
  - 
  - @param eventId    unique event identifier
  - @param occurredAt timestamp of the odds change
  - @param matchId    the match this market belongs to
  - @param market     e.g. "MATCH_WINNER", "OVER_UNDER_2_5"
  - @param outcome    e.g. "HOME", "AWAY", "DRAW"
  - @param oldOdds    previous decimal odds
  - @param newOdds    updated decimal odds
    */
    record OddsUpdated(
    String eventId,
    Instant occurredAt,
    String matchId,
    String market,
    String outcome,
    double oldOdds,
    double newOdds
    ) implements BettingEvent {}
  
  /**
  - Fired when a customer places a bet.
  - 
  - @param eventId    unique event identifier
  - @param occurredAt timestamp of the bet
  - @param betId      unique bet slip identifier
  - @param customerId customer placing the bet
  - @param matchId    match being bet on
  - @param outcome    selected outcome
  - @param odds       odds at time of placement
  - @param stake      amount wagered (in minor currency units, e.g. cents)
    */
    record BetPlaced(
    String eventId,
    Instant occurredAt,
    String betId,
    String customerId,
    String matchId,
    String market,
    String outcome,
    double odds,
    long stake
    ) implements BettingEvent {}
    }