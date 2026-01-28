package com.dirty.code.repository;

import com.dirty.code.repository.model.DirtyUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<DirtyUser, UUID> {
    Optional<DirtyUser> findByFirebaseUid(String uid);
    boolean existsByFirebaseUid(String uid);
}
