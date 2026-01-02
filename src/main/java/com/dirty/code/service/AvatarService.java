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
                .stamina(request.getStamina() != null ? request.getStamina() : 0)
                .str(request.getStr() != null ? request.getStr() : 0)
                .karma(request.getKarma() != null ? request.getKarma() : 0)
                .intelligence(request.getIntelligence() != null ? request.getIntelligence() : 0)
                .active(true)
                .user(user)
                .build();

        Avatar savedAvatar = avatarRepository.save(avatar);
        log.info("Avatar created with ID: {}", savedAvatar.getId());

        return AvatarResponseDTO.builder()
                .id(savedAvatar.getId())
                .name(savedAvatar.getName())
                .stamina(savedAvatar.getStamina())
                .str(savedAvatar.getStr())
                .karma(savedAvatar.getKarma())
                .intelligence(savedAvatar.getIntelligence())
                .active(savedAvatar.getActive())
                .build();
    }
}
