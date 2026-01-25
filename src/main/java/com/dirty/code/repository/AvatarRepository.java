package com.dirty.code.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.dirty.code.repository.model.Avatar;

@Repository
public interface AvatarRepository extends JpaRepository<Avatar, UUID> {
    boolean existsByNameAndActiveTrue(String name);
    @Query("SELECT a FROM Avatar a, User u WHERE a.userId = u.id AND u.firebaseUid = :firebaseUid AND a.active = true")
    Optional<Avatar> findByFirebaseUidAndActiveTrue(@Param("firebaseUid") String firebaseUid);

    @Query("SELECT a FROM Avatar a WHERE a.userId = :userId AND a.active = true")
    Optional<Avatar> findByUserIdAndActiveTrue(@Param("userId") UUID userId);

    List<Avatar> findTop10ByActiveTrueOrderByLevelDescExperienceDesc();

    List<Avatar> findByActiveTrue();
    
    @Query("SELECT a FROM Avatar a WHERE a.active = true AND a.statusCooldown IS NOT NULL AND a.statusCooldown <= :now")
    List<Avatar> findByStatusCooldownExpired(@Param("now") LocalDateTime now);

    @Modifying
    @Query("UPDATE Avatar a SET a.stamina = :stamina WHERE a.id = :avatarId")
    void updateStamina(@Param("avatarId") UUID avatarId, @Param("stamina") Integer stamina);

    @Modifying
    @Query("UPDATE Avatar a SET a.life = :life WHERE a.id = :avatarId")
    void updateLife(@Param("avatarId") UUID avatarId, @Param("life") Integer life);

    @Modifying
    @Query("UPDATE Avatar a SET a.stamina = :stamina, a.life = :life WHERE a.id = :avatarId")
    void updateStaminaAndLife(@Param("avatarId") UUID avatarId, 
                              @Param("stamina") Integer stamina, 
                              @Param("life") Integer life);
}
