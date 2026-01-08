package com.dirty.code.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dirty.code.repository.model.Avatar;

@Repository
public interface AvatarRepository extends JpaRepository<Avatar, UUID> {
    boolean existsByNameAndActiveTrue(String name);
    Optional<Avatar> findByUserFirebaseUidAndActiveTrue(String firebaseUid);

    List<Avatar> findTop10ByActiveTrueOrderByLevelDescExperienceDesc();
}
