package com.dirty.code.repository;

import com.dirty.code.repository.model.Avatar;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface AvatarRepository extends JpaRepository<Avatar, UUID> {
    boolean existsByNameAndActiveTrue(String name);
    Optional<Avatar> findByUserFirebaseUidAndActiveTrue(String firebaseUid);
}
