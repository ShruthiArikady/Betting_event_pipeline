package com.kambi.betting.api;

import com.kambi.betting.model.BetDocument;
import com.kambi.betting.model.OddsDocument;
import com.kambi.betting.repository.BetRepository;
import com.kambi.betting.repository.OddsRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**

- REST API to query betting data stored in MongoDB.
- Demonstrates the full pipeline: Producer → Kafka → Consumer → MongoDB → REST.
  */
  @RestController
  @RequestMapping("/api/v1")
  public class BettingController {
  
  private final BetRepository  betRepository;
  private final OddsRepository oddsRepository;
  
  public BettingController(BetRepository betRepository, OddsRepository oddsRepository) {
  this.betRepository  = betRepository;
  this.oddsRepository = oddsRepository;
  }
  
  // ── Bets ─────────────────────────────────────────────────────────────────
  
  /** GET /api/v1/bets — all bets */
  @GetMapping("/bets")
  public List<BetDocument> getAllBets() {
  return betRepository.findAll();
  }
  
  /** GET /api/v1/bets/{betId} — single bet by ID */
  @GetMapping("/bets/{betId}")
  public ResponseEntity<BetDocument> getBetById(@PathVariable String betId) {
  return betRepository.findByBetId(betId)
  .map(ResponseEntity::ok)
  .orElse(ResponseEntity.notFound().build());
  }
  
  /** GET /api/v1/bets?customerId=cust:alice — bets by customer */
  @GetMapping(value = "/bets", params = "customerId")
  public List<BetDocument> getBetsByCustomer(@RequestParam String customerId) {
  return betRepository.findByCustomerId(customerId);
  }
  
  /** GET /api/v1/bets?matchId=match:arsenal_vs_chelsea — bets on a match */
  @GetMapping(value = "/bets", params = "matchId")
  public List<BetDocument> getBetsByMatch(@RequestParam String matchId) {
  return betRepository.findByMatchId(matchId);
  }
  
  // ── Odds ─────────────────────────────────────────────────────────────────
  
  /** GET /api/v1/odds — all odds updates */
  @GetMapping("/odds")
  public List<OddsDocument> getAllOdds() {
  return oddsRepository.findAll();
  }
  
  /** GET /api/v1/odds?matchId=match:arsenal_vs_chelsea — odds for a match */
  @GetMapping(value = "/odds", params = "matchId")
  public List<OddsDocument> getOddsByMatch(@RequestParam String matchId) {
  return oddsRepository.findByMatchId(matchId);
  }
  
  /** GET /api/v1/odds?matchId=…&market=MATCH_WINNER — odds for a specific market */
  @GetMapping(value = "/odds", params = {"matchId", "market"})
  public List<OddsDocument> getOddsByMatchAndMarket(
  @RequestParam String matchId,
  @RequestParam String market) {
  return oddsRepository.findByMatchIdAndMarket(matchId, market);
  }
  }