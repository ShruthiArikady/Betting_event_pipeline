package com.kambi.betting.repository;

import com.kambi.betting.Entity.BettingEventEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BettingEventRepository extends JpaRepository<BettingEventEntity, Long> {
}