package com.dirty.code.controller;

import java.util.List;
import java.util.Map;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import com.dirty.code.dto.AvatarCreateRequestDTO;
import com.dirty.code.dto.AvatarResponseDTO;
import com.dirty.code.dto.AvatarUpdateRequestDTO;
import com.dirty.code.repository.model.Attribute;

import java.util.UUID;

@RestController
@RequestMapping("/v1/avatars")
public interface AvatarController {

    @GetMapping("/{id}")
    AvatarResponseDTO getAvatar(@PathVariable UUID id);

    @PostMapping
    AvatarResponseDTO createAvatar(@AuthenticationPrincipal String uid, @RequestBody AvatarCreateRequestDTO request);

    @PutMapping
    AvatarResponseDTO updateAvatar(@AuthenticationPrincipal String uid, @RequestBody AvatarUpdateRequestDTO request);

    @GetMapping("/ranking")
    List<AvatarResponseDTO> getRanking();

    @PostMapping("/attributes/increase")
    AvatarResponseDTO increaseAttribute(@AuthenticationPrincipal String uid, @RequestParam Attribute attribute);

    @GetMapping("/check-name")
    Map<String, Boolean> checkNameAvailability(@RequestParam String name);
}
