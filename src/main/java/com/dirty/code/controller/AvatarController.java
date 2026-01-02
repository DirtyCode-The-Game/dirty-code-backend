package com.dirty.code.controller;

import com.dirty.code.dto.AvatarCreateRequestDTO;
import com.dirty.code.dto.AvatarResponseDTO;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/avatars")
public interface AvatarController {

    @PostMapping
    AvatarResponseDTO createAvatar(@AuthenticationPrincipal String uid, @RequestBody AvatarCreateRequestDTO request);
}
