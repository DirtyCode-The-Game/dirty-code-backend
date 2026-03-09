package com.dirty.code.repository;

import com.dirty.code.repository.model.AvatarSpecialAction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface AvatarSpecialActionRepository extends JpaRepository<AvatarSpecialAction, UUID> {
}
