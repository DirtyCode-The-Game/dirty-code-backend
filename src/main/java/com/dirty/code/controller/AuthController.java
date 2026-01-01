package com.dirty.code.controller;

import com.dirty.code.dto.UserDTO;
import com.dirty.code.repository.UserRepository;
import com.dirty.code.repository.model.User;
import com.google.firebase.auth.FirebaseAuth;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserRepository userRepository;

    @GetMapping("/me")
    public UserDTO getMe(Authentication authentication) throws Exception {
        String uid = (String) authentication.getPrincipal();
        User user = userRepository.findById(uid)
                .orElseThrow(() -> new RuntimeException("User not found in local database"));

        return new UserDTO(
                user.getUid(),
                user.getName(),
                user.getEmail(),
                user.getPhotoUrl()
        );
    }

    @PostMapping("/token/{uid}")
    public Map<String, String> getCustomToken(@PathVariable String uid) throws Exception {
        String customToken = FirebaseAuth.getInstance().createCustomToken(uid);
        return Map.of("customToken", customToken);
    }
}
