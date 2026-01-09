package com.dirty.code.controller;

import com.dirty.code.dto.ActionResultDTO;
import com.dirty.code.dto.AvatarResponseDTO;
import com.dirty.code.dto.GameActionDTO;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/v1/actions")
public interface GameActionController {
    @GetMapping("/type/{type}")
    List<GameActionDTO> getActionsByType(@AuthenticationPrincipal String uid, @PathVariable String type);

    @PostMapping("/{actionId}/perform")
    ActionResultDTO performAction(@AuthenticationPrincipal String uid, @PathVariable UUID actionId);
}
