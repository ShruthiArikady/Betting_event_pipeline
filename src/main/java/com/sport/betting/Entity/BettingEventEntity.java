package com.kambi.betting.Entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "betting_events")
public class BettingEventEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String eventType;
    private String market;
    private Double odds;
    private LocalDateTime timestamp;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getEventType() { return eventType; }
    public void setEventType(String eventType) { this.eventType = eventType; }

    public String getMarket() { return market; }
    public void setMarket(String market) { this.market = market; }

    public Double getOdds() { return odds; }
    public void setOdds(Double odds) { this.odds = odds; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
}