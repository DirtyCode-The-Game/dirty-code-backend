package com.dirty.code.controller;

import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dirty.code.dto.AvatarCreateRequestDTO;
import com.dirty.code.dto.AvatarResponseDTO;
import com.dirty.code.dto.AvatarUpdateRequestDTO;
import com.dirty.code.repository.model.Attribute;

@RestController
@RequestMapping("/v1/avatars")
public interface AvatarController {

    @PostMapping
    AvatarResponseDTO createAvatar(@AuthenticationPrincipal String uid, @RequestBody AvatarCreateRequestDTO request);

    @PutMapping
    AvatarResponseDTO updateAvatar(@AuthenticationPrincipal String uid, @RequestBody AvatarUpdateRequestDTO request);

    @GetMapping("/ranking")
    List<AvatarResponseDTO> getRanking();

    @PostMapping("/attributes/increase")
    AvatarResponseDTO increaseAttribute(@AuthenticationPrincipal String uid, @RequestParam Attribute attribute);
}
