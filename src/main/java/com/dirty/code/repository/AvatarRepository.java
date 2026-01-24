package com.dirty.code.repository;

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
    Optional<Avatar> findByUserFirebaseUidAndActiveTrue(String firebaseUid);

    List<Avatar> findTop10ByActiveTrueOrderByLevelDescExperienceDesc();

    List<Avatar> findByActiveTrue();

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
