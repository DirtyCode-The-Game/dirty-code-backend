package com.dirty.code.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dirty.code.controller.UserController;
import com.dirty.code.dto.AvatarResponseDTO;
import com.dirty.code.dto.UserResponseDTO;
import com.dirty.code.exception.ResourceNotFoundException;
import com.dirty.code.repository.UserRepository;
import com.dirty.code.repository.model.Avatar;
import com.dirty.code.repository.model.User;
import com.google.firebase.auth.UserRecord;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService implements UserController {

    private final UserRepository userRepository;
    private final AvatarService avatarService;

    @Override
    public UserResponseDTO getMe(String uid) {
        log.info("Fetching current user info for UID: {}", uid);
        return userRepository.findByFirebaseUid(uid)
                .map(this::mapToResponseDTO)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with UID: " + uid));
    }

    private UserResponseDTO mapToResponseDTO(User user) {
        Avatar activeAvatar = user.getAvatars() != null ? user.getAvatars().stream()
                .filter(a -> Boolean.TRUE.equals(a.getActive()))
                .findFirst()
                .orElse(null) : null;

        if (activeAvatar != null) {
            // Auto-clear expired timeout (hospital/jail) if present
            avatarService.clearExpiredTimeout(activeAvatar);
        }

        AvatarResponseDTO avatarDTO = null;
        if (activeAvatar != null) {
            avatarDTO = AvatarResponseDTO.fromAvatar(activeAvatar);
        }

        return UserResponseDTO.builder()
                .id(user.getId())
                .uid(user.getFirebaseUid())
                .name(user.getName())
                .email(user.getEmail())
                .photoUrl(user.getPhotoUrl())
                .activeAvatar(avatarDTO)
                .build();
    }

    @Transactional
    public void saveOrUpdateUser(UserRecord decodedToken) {
        String uid = decodedToken.getUid();
        log.info("Saving or updating user from UserRecord with UID: {}", uid);
        String email = decodedToken.getEmail();
        String name = decodedToken.getDisplayName();
        String picture = decodedToken.getPhotoUrl();

        saveOrUpdateUser(uid, email, name, picture);
        log.info("Successfully processed UserRecord for UID: {}", uid);
    }

    public boolean existsByUid(String uid) {
        log.info("Checking existence of user with UID: {}", uid);
        boolean exists = userRepository.existsByFirebaseUid(uid);
        log.info("User with UID: {} exists: {}", uid, exists);
        return exists;
    }

    @Transactional
    public void saveOrUpdateUser(String uid, String email, String name, String picture) {
        userRepository.findByFirebaseUid(uid)
                .map(existingUser -> {
                    log.info("Updating existing user with UID: {}", uid);
                    existingUser.setName(name);
                    existingUser.setEmail(email);
                    existingUser.setPhotoUrl(picture);
                    return userRepository.save(existingUser);
                })
                .orElseGet(() -> {
                    log.info("Creating new user with UID: {}", uid);
                    User newUser = User.builder()
                            .firebaseUid(uid)
                            .name(name)
                            .email(email)
                            .photoUrl(picture)
                            .build();
                    return userRepository.save(newUser);
                });
    }
}
