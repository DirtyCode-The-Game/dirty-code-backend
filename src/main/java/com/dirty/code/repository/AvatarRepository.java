package com.dirty.code.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dirty.code.repository.model.Avatar;
import com.dirty.code.repository.model.DirtyUser;

@Repository
public interface AvatarRepository extends JpaRepository<Avatar, UUID> {
    boolean existsByNameAndActiveTrue(String name);

    Optional<Avatar> findByUserAndActiveTrue(DirtyUser user);

    List<Avatar> findTop10ByActiveTrueOrderByLevelDescExperienceDesc();

    List<Avatar> findByActiveTrue();

    List<Avatar> findByActiveTrueAndStatusCooldownIsNotNullAndStatusCooldownLessThanEqual(LocalDateTime now);
}
