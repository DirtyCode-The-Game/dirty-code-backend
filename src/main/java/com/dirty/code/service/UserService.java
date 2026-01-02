package com.dirty.code.service;

import com.dirty.code.controller.UserController;
import com.dirty.code.dto.UserResponseDTO;
import com.dirty.code.repository.UserRepository;
import com.dirty.code.repository.model.User;
import com.google.firebase.auth.UserRecord;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService implements UserController {

    private final UserRepository userRepository;

    @Override
    public List<UserResponseDTO> getAllUsers() {
        log.info("Fetching all users");
        List<UserResponseDTO> users = userRepository.findAll().stream()
                .map(user -> UserResponseDTO.builder()
                        .uid(user.getUid())
                        .name(user.getName())
                        .email(user.getEmail())
                        .photoUrl(user.getPhotoUrl())
                        .build())
                .collect(Collectors.toList());
        log.info("Found {} users", users.size());
        return users;
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
        boolean exists = userRepository.existsById(uid);
        log.info("User with UID: {} exists: {}", uid, exists);
        return exists;
    }

    private void saveOrUpdateUser(String uid, String email, String name, String picture) {
        userRepository.findById(uid)
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
                            .uid(uid)
                            .name(name)
                            .email(email)
                            .photoUrl(picture)
                            .build();
                    return userRepository.save(newUser);
                });
    }
}
