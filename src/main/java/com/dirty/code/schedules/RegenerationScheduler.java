package com.dirty.code.schedules;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dirty.code.repository.AvatarRepository;
import com.dirty.code.repository.model.Avatar;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class RegenerationScheduler {

    private final AvatarRepository avatarRepository;

    private static final int STAMINA_REGEN_PER_MINUTE = 1;
    private static final int LIFE_REGEN_PER_MINUTE = 1;
    private static final int MAX_STAMINA = 100;
    private static final int MAX_LIFE = 100;

    public RegenerationScheduler(AvatarRepository avatarRepository) {
        this.avatarRepository = avatarRepository;
    }

    @Scheduled(fixedDelay = 60000) // Every 60 seconds after previous execution completes
    @Transactional
    public void regenerateAllAvatars() {
        log.debug("Starting passive regeneration for all active avatars");

        List<Avatar> activeAvatars = avatarRepository.findByActiveTrue();
        int regeneratedCount = 0;

        for (Avatar avatar : activeAvatars) {
            if (avatar.getTimeout() != null && avatar.getTimeout().isAfter(LocalDateTime.now())) {
                continue;
            }

            boolean wasRegenerated = regenerateAvatar(avatar);
            if (wasRegenerated) {
                regeneratedCount++;
            }
        }

        if (regeneratedCount > 0) {
            log.info("Regenerated {} active avatars", regeneratedCount);
        }
    }

    private boolean regenerateAvatar(Avatar avatar) {
        boolean needsUpdate = false;

        Integer staminaValue = avatar.getStamina();
        Integer lifeValue = avatar.getLife();
        int currentStamina = (staminaValue != null) ? staminaValue : 0;
        int currentLife = (lifeValue != null) ? lifeValue : 0;

        Integer newStamina = null;
        Integer newLife = null;

        if (currentStamina < MAX_STAMINA) {
            newStamina = Math.min(currentStamina + STAMINA_REGEN_PER_MINUTE, MAX_STAMINA);
            needsUpdate = true;
        }

        if (currentLife < MAX_LIFE) {
            newLife = Math.min(currentLife + LIFE_REGEN_PER_MINUTE, MAX_LIFE);
            needsUpdate = true;
        }

        if (needsUpdate) {
            if (newStamina != null) {
                avatar.setStamina(newStamina);
            }
            if (newLife != null) {
                avatar.setLife(newLife);
            }
            avatarRepository.save(avatar);
            log.debug("Regenerated avatar {} - Stamina: {} -> {}, Life: {} -> {}", 
                     avatar.getName(), currentStamina, avatar.getStamina(), currentLife, avatar.getLife());
        }

        return needsUpdate;
    }
}
