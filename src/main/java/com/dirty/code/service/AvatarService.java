package com.dirty.code.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dirty.code.controller.AvatarController;
import com.dirty.code.dto.AvatarCreateRequestDTO;
import com.dirty.code.dto.AvatarResponseDTO;
import com.dirty.code.dto.AvatarUpdateRequestDTO;
import com.dirty.code.exception.BusinessException;
import com.dirty.code.exception.ResourceNotFoundException;
import com.dirty.code.repository.AvatarRepository;
import com.dirty.code.repository.UserRepository;
import com.dirty.code.repository.model.Attribute;
import com.dirty.code.repository.model.Avatar;
import com.dirty.code.repository.model.User;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class AvatarService implements AvatarController {

    private final AvatarRepository avatarRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public AvatarResponseDTO createAvatar(String uid, AvatarCreateRequestDTO request) {
        log.info("Creating avatar for user UID: {}", uid);

        if (avatarRepository.existsByNameAndActiveTrue(request.getName())) {
            throw new BusinessException("Avatar name already exists and is active: " + request.getName());
        }

        User user = userRepository.findByFirebaseUid(uid)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with UID: " + uid));

        Avatar avatar = Avatar.builder()
                .name(request.getName())
                .picture(request.getPicture())
                .story(request.getStory())
                .level(0)
                .experience(0)
                .totalExperience(0)
                .nextLevelExperience(com.dirty.code.utils.GameFormulas.getBaseExperience())
                .stamina(100)
                .life(100)
                .money(BigDecimal.valueOf(500))
                .availablePoints(0)
                .intelligence(0)
                .charisma(0)
                .strength(0)
                .stealth(0)
                .hacking(0)
                .work(0)
                .active(true)
                .user(user)
                .build();

        Avatar savedAvatar = avatarRepository.save(avatar);
        log.info("Avatar created with ID: {}", savedAvatar.getId());

        return AvatarResponseDTO.fromAvatar(savedAvatar);
    }

    @Override
    @Transactional
    public AvatarResponseDTO updateAvatar(String uid, AvatarUpdateRequestDTO request) {
        log.info("Updating avatar for user UID: {}", uid);

        Avatar avatar = avatarRepository.findByUserFirebaseUidAndActiveTrue(uid)
                .orElseThrow(() -> new ResourceNotFoundException("Active avatar not found for user UID: " + uid));

        // Use current values if request values are null
        int currentInt = avatar.getIntelligence();
        int currentCha = avatar.getCharisma();
        int currentStr = avatar.getStrength();
        int currentSte = avatar.getStealth();
        int currentAvailable = avatar.getAvailablePoints();

        int newInt = request.getIntelligence();
        int newCha = request.getCharisma();
        int newStr = request.getStrength();
        int newSte = request.getStealth();

        // Ensure stats are not being decreased
        if (newInt < currentInt || newCha < currentCha || newStr < currentStr || newSte < currentSte) {
            throw new BusinessException("Stats cannot be decreased.");
        }

        int currentStatsSum = currentInt + currentCha + currentStr + currentSte;
        int newStatsSum = newInt + newCha + newStr + newSte;
        int cost = newStatsSum - currentStatsSum;

        if (cost > currentAvailable) {
            throw new BusinessException("Not enough available points. Required: " + cost + ", Available: " + currentAvailable);
        }

        avatar.setIntelligence(newInt);
        avatar.setCharisma(newCha);
        avatar.setStrength(newStr);
        avatar.setStealth(newSte);
        avatar.setAvailablePoints(currentAvailable - cost);

        if (request.getStory() != null) {
            avatar.setStory(request.getStory());
        }

        Avatar savedAvatar = avatarRepository.save(avatar);
        log.info("Avatar updated with ID: {}", savedAvatar.getId());

        return AvatarResponseDTO.fromAvatar(savedAvatar);
    }


    @Override
    @Transactional(readOnly = true)
    public List<AvatarResponseDTO> getRanking() {
        return avatarRepository.findTop10ByActiveTrueOrderByLevelDescExperienceDesc()
                .stream()
                .map(AvatarResponseDTO::fromAvatar)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public AvatarResponseDTO increaseAttribute(String uid, Attribute attribute) {
        log.info("Increasing attribute {} for user UID: {}", attribute, uid);

        Avatar avatar = avatarRepository.findByUserFirebaseUidAndActiveTrue(uid)
                .orElseThrow(() -> new ResourceNotFoundException("Active avatar not found for user UID: " + uid));

        if (avatar.getAvailablePoints() <= 0) {
            throw new BusinessException("No available points to distribute.");
        }

        switch (attribute) {
            case STRENGTH:
                avatar.setStrength((avatar.getStrength() != null ? avatar.getStrength() : 0) + 1);
                break;
            case INTELLIGENCE:
                avatar.setIntelligence((avatar.getIntelligence() != null ? avatar.getIntelligence() : 0) + 1);
                break;
            case CHARISMA:
                avatar.setCharisma((avatar.getCharisma() != null ? avatar.getCharisma() : 0) + 1);
                break;
            case STEALTH:
                avatar.setStealth((avatar.getStealth() != null ? avatar.getStealth() : 0) + 1);
                break;
            default:
                throw new BusinessException("Invalid attribute: " + attribute);
        }

        avatar.setAvailablePoints(avatar.getAvailablePoints() - 1);
        Avatar savedAvatar = avatarRepository.save(avatar);
        
        return AvatarResponseDTO.fromAvatar(savedAvatar);
    }

    @Override
    @Transactional(readOnly = true)
    public java.util.Map<String, Boolean> checkNameAvailability(String name) {
        log.info("Checking availability for avatar name: {}", name);
        boolean exists = avatarRepository.existsByNameAndActiveTrue(name);
        return Map.of("available", !exists);
    }

    /**
     * Checks if avatar has an expired timeout and automatically clears it,
     * restoring the avatar to normal state (HP=100, stamina=100).
     *
     * @param avatar The avatar to check and potentially restore
     */
    @Transactional
    public void clearExpiredTimeout(Avatar avatar) {
        if (avatar == null || avatar.getTimeout() == null) {
            return;
        }

        if (LocalDateTime.now().isAfter(avatar.getTimeout())) {
            log.info("Auto-clearing expired timeout for avatar {}: type={}, expired at={}", 
                    avatar.getName(), avatar.getTimeoutType(), avatar.getTimeout());
            
            avatar.setTimeout(null);
            avatar.setTimeoutType(null);
            avatar.setLife(100);
            avatar.setStamina(100);
            
            avatarRepository.save(avatar);
            log.info("Avatar {} automatically restored after timeout expiration", avatar.getName());

        }
    }
}
