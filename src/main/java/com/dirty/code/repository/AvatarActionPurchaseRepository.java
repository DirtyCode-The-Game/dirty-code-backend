package com.dirty.code.repository;

import com.dirty.code.repository.model.AvatarActionPurchase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface AvatarActionPurchaseRepository extends JpaRepository<AvatarActionPurchase, UUID> {
    Optional<AvatarActionPurchase> findByAvatarIdAndActionId(UUID avatarId, UUID actionId);
}
