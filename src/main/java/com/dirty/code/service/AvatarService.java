package com.dirty.code.service;

import java.math.BigDecimal;

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
                .level(0)
                .experience(0)
                .stamina(100)
                .life(100)
                .money(BigDecimal.valueOf(500))
                .availablePoints(0)
                .intelligence(0)
                .charisma(0)
                .stealth(0)
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
        int newStr = request.getStreetIntelligence();
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

        if (request.getName() != null && !request.getName().equals(avatar.getName())) {
            if (avatarRepository.existsByNameAndActiveTrue(request.getName())) {
                throw new BusinessException("Avatar name already exists: " + request.getName());
            }
            avatar.setName(request.getName());
        }

        if (request.getPicture() != null) {
            avatar.setPicture(request.getPicture());
        }

        Avatar savedAvatar = avatarRepository.save(avatar);
        log.info("Avatar updated with ID: {}", savedAvatar.getId());

        return AvatarResponseDTO.fromAvatar(savedAvatar);
    }
}
