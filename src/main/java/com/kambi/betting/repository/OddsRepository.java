package com.kambi.betting.repository;

import com.kambi.betting.model.OddsDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OddsRepository extends MongoRepository<OddsDocument, String> {

List<OddsDocument> findByMatchId(String matchId);

List<OddsDocument> findByMatchIdAndMarket(String matchId, String market);


}