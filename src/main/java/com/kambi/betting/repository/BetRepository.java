package com.kambi.betting.repository;

import com.kambi.betting.model.BetDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BetRepository extends MongoRepository<BetDocument, String> {

List<BetDocument> findByCustomerId(String customerId);

List<BetDocument> findByMatchId(String matchId);

Optional<BetDocument> findByBetId(String betId);


}