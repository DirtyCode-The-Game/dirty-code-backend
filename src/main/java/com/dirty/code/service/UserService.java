package com.dirty.code.service;

import com.dirty.code.repository.UserRepository;
import com.dirty.code.repository.model.User;
import com.google.firebase.auth.FirebaseToken;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    @Transactional
    public User saveOrUpdateUser(FirebaseToken decodedToken) {
        String uid = decodedToken.getUid();
        String email = decodedToken.getEmail();
        String name = (String) decodedToken.getClaims().get("name");
        String picture = (String) decodedToken.getClaims().get("picture");

        return userRepository.findById(uid)
                .map(existingUser -> {
                    existingUser.setName(name);
                    existingUser.setEmail(email);
                    existingUser.setPhotoUrl(picture);
                    return userRepository.save(existingUser);
                })
                .orElseGet(() -> {
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
