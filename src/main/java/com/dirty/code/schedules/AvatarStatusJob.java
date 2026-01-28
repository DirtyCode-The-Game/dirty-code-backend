package com.dirty.code.schedules;

import com.dirty.code.repository.AvatarRepository;
import com.dirty.code.repository.model.Avatar;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AvatarStatusJob {

    private final AvatarRepository avatarRepository;

    @Scheduled(fixedDelay = 60000)
    @Transactional
    public void clearExpiredCooldowns() {
        LocalDateTime now = LocalDateTime.now();
        log.debug("Running job to clear expired status cooldowns at {}", now);

        List<Avatar> expiredAvatars = avatarRepository.findByActiveTrueAndStatusCooldownIsNotNullAndStatusCooldownLessThanEqual(now);

        if (expiredAvatars.isEmpty()) {
            return;
        }

        log.info("Found {} avatars with expired status cooldown. Clearing temporary stats.", expiredAvatars.size());

        expiredAvatars.forEach(avatar -> {
            avatar.setTemporaryStrength(0);
            avatar.setTemporaryIntelligence(0);
            avatar.setTemporaryCharisma(0);
            avatar.setTemporaryStealth(0);
            avatar.setStatusCooldown(null);
        });

        avatarRepository.saveAll(expiredAvatars);
        log.info("Successfully cleared temporary stats for {} avatars.", expiredAvatars.size());
    }
}
