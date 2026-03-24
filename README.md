# 🏆 Betting Event Pipeline

A real-time sports betting event pipeline built with **Java 21**, **Apache Kafka**, **Spring Boot 3**, and **MongoDB** — inspired by event-driven architectures used in betting platforms like Kambi.

-----

## Architecture

```
┌─────────────────────────────┐
│   BettingEventSimulator     │  Simulates live sports data feeds
│   (Scheduled producer)      │  (odds changes every 3s, bets every 7s)
└────────────┬────────────────┘
             │ publishes to
             ▼
┌─────────────────────────────┐
│        Apache Kafka         │
│                             │
│  Topic: odds-updated (3p)   │  3 partitions = 3 parallel consumers
│  Topic: bet-placed   (3p)   │  Key = matchId/customerId → ordering
└────────────┬────────────────┘
             │ consumed by
             ▼
┌─────────────────────────────┐
│   BettingEventConsumer      │  Java 21 pattern matching switch
│   (@KafkaListener)          │  routes OddsUpdated vs BetPlaced
└────────────┬────────────────┘
             │ persists to
             ▼
┌─────────────────────────────┐
│         MongoDB             │
│                             │
│  Collection: bets           │
│  Collection: odds           │
└────────────┬────────────────┘
             │ queried via
             ▼
┌─────────────────────────────┐
│   REST API (Spring Boot)    │  GET /api/v1/bets
│   BettingController         │  GET /api/v1/odds
└─────────────────────────────┘
```

-----

## Java 21 Features Used

|Feature                    |Where                                                                 |
|---------------------------|----------------------------------------------------------------------|
|**Sealed interfaces**      |`BettingEvent` — enforces exhaustive event handling                   |
|**Records**                |`BettingEvent.OddsUpdated`, `BetPlaced`, `BetDocument`, `OddsDocument`|
|**Pattern matching switch**|`BettingEventConsumer` — routes event types at compile-time safety    |
|**Virtual threads**        |Enabled globally via `spring.threads.virtual.enabled=true`            |
|**`CompletableFuture`**    |Async Kafka send in `BettingEventProducer`                            |

-----

## Tech Stack

- **Java 21**
- **Spring Boot 3.2**
- **Apache Kafka** (via `spring-kafka`)
- **MongoDB** (via `spring-data-mongodb`)
- **Docker Compose** (Kafka + Zookeeper + MongoDB)

-----

## Getting Started

### Prerequisites

- Java 21+
- Docker + Docker Compose
- Maven 3.9+

### 1. Start infrastructure

```bash
docker-compose up -d
```

### 2. Run the application

```bash
mvn spring-boot:run
```

The simulator starts immediately — you’ll see Kafka events being produced and consumed in the logs.

### 3. Query the REST API

```bash
# All bets
curl http://localhost:8080/api/v1/bets

# Bets by customer
curl "http://localhost:8080/api/v1/bets?customerId=cust:alice"

# Bets by match
curl "http://localhost:8080/api/v1/bets?matchId=match:arsenal_vs_chelsea"

# All odds updates
curl http://localhost:8080/api/v1/odds

# Odds for a specific match + market
curl "http://localhost:8080/api/v1/odds?matchId=match:arsenal_vs_chelsea&market=MATCH_WINNER"
```

-----

## Key Design Decisions

### Kafka message keys

Events are keyed by `matchId` (for odds) and `customerId` (for bets). This guarantees that all events for the same match/customer land in the **same partition**, preserving ordering — critical for bet settlement and audit trails.

### Sealed interfaces + pattern matching

`BettingEvent` is a sealed interface with two permitted record implementations. The consumer uses a Java 21 pattern matching `switch` — if a new event type is ever added, the **compiler enforces** that all consumers handle it. No missed cases.

### Virtual threads

`spring.threads.virtual.enabled=true` enables Java 21 virtual threads for all Spring-managed threads. Since both Kafka consumer callbacks and HTTP handlers are I/O-bound (MongoDB writes/reads), virtual threads provide much higher throughput with the same hardware.

### At-least-once delivery

Default Kafka consumer semantics. Offsets are committed after successful processing. If the consumer crashes mid-processing, the event will be re-delivered. For idempotent handling, `betId` and `eventId` are stored and could be used for deduplication.

-----

## Project Structure

```
betting-event-pipeline/
├── docker-compose.yml
├── pom.xml
└── src/main/java/com/kambi/betting/
    ├── BettingEventPipelineApplication.java
    ├── api/
    │   └── BettingController.java
    ├── config/
    │   └── KafkaTopicConfig.java
    ├── consumer/
    │   └── BettingEventConsumer.java
    ├── model/
    │   ├── BettingEvent.java       ← sealed interface + records
    │   ├── BetDocument.java
    │   └── OddsDocument.java
    ├── producer/
    │   ├── BettingEventProducer.java
    │   └── BettingEventSimulator.java
    └── repository/
        ├── BetRepository.java
        └── OddsRepository.java
