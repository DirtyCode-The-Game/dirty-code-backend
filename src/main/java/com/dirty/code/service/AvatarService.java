package com.dirty.code.service;

import com.dirty.code.controller.AvatarController;
import com.dirty.code.dto.AvatarCreateRequestDTO;
import com.dirty.code.dto.AvatarResponseDTO;
import com.dirty.code.exception.BusinessException;
import com.dirty.code.exception.ResourceNotFoundException;
import com.dirty.code.repository.AvatarRepository;
import com.dirty.code.repository.UserRepository;
import com.dirty.code.repository.model.Avatar;
import com.dirty.code.repository.model.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

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
                .streetIntelligence(0)
                .stealth(0)
                .active(true)
                .user(user)
                .build();

        Avatar savedAvatar = avatarRepository.save(avatar);
        log.info("Avatar created with ID: {}", savedAvatar.getId());

        return AvatarResponseDTO.fromAvatar(savedAvatar);
    }
}
