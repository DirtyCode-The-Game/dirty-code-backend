package com.dirty.code.repository;

import com.dirty.code.repository.model.GameAction;
import com.dirty.code.repository.model.GameActionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface GameActionRepository extends JpaRepository<GameAction, UUID> {
    List<GameAction> findByType(GameActionType type);
}
