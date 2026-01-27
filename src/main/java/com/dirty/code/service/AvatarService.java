package com.dirty.code.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;
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
    private final AvatarTimeoutService timeoutService;

    @Override
    @Transactional(readOnly = true)
    public AvatarResponseDTO getAvatar(UUID id) {
        log.info("Fetching avatar with ID: {}", id);
        return avatarRepository.findById(id)
                .map(AvatarResponseDTO::fromAvatar)
                .orElseThrow(() -> new ResourceNotFoundException("Avatar not found with ID: " + id));
    }

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
                .currentStamina(100)
                .maxStamina(100)
                .currentLife(100)
                .maxLife(100)
                .money(BigDecimal.valueOf(500))
                .availablePoints(0)
                .intelligence(0)
                .charisma(0)
                .strength(0)
                .stealth(0)
                .hacking(0)
                .work(0)
                .active(true)
                .userId(user.getId())
                .build();

        Avatar savedAvatar = avatarRepository.save(avatar);
        log.info("Avatar created with ID: {}", savedAvatar.getId());

        return AvatarResponseDTO.fromAvatar(savedAvatar);
    }

    @Override
    @Transactional
    public AvatarResponseDTO updateAvatar(String uid, AvatarUpdateRequestDTO request) {
        log.info("Updating avatar for user UID: {}", uid);

        Avatar avatar = avatarRepository.findByFirebaseUidAndActiveTrue(uid)
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

        if (newStr > currentStr) {
            int strGained = newStr - currentStr;
            avatar.setCurrentLife(Math.min(avatar.getMaxLife() + (newStr * 10), avatar.getCurrentLife() + (strGained * 10)));
            avatar.setCurrentStamina(Math.min(avatar.getMaxStamina() + (newStr * 10), avatar.getCurrentStamina() + (strGained * 10)));
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

        Avatar avatar = avatarRepository.findByFirebaseUidAndActiveTrue(uid)
                .orElseThrow(() -> new ResourceNotFoundException("Active avatar not found for user UID: " + uid));

        if (avatar.getAvailablePoints() <= 0) {
            throw new BusinessException("No available points to distribute.");
        }

        switch (attribute) {
            case STRENGTH:
                avatar.setStrength((avatar.getStrength() != null ? avatar.getStrength() : 0) + 1);
                avatar.increaseMaxLifeAndStamina();
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
        timeoutService.processExpiredTimeoutSilently(avatar);
    }
}
