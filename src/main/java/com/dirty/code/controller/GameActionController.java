package com.dirty.code.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dirty.code.dto.ActionResultDTO;
import com.dirty.code.dto.GameActionDTO;
import com.dirty.code.repository.model.GameActionType;

@RestController
@RequestMapping("/v1/actions")
public interface GameActionController {

    @GetMapping("/type/{type}")
    List<GameActionDTO> getActionsByType(@AuthenticationPrincipal String uid, @PathVariable GameActionType type);

    @PostMapping("/{actionId}/perform")
    ActionResultDTO performAction(@AuthenticationPrincipal String uid, @PathVariable UUID actionId, @RequestParam(defaultValue = "1") Integer times);

    @PostMapping("/timeout/leave")
    ActionResultDTO leaveTimeout(@AuthenticationPrincipal String uid, @RequestParam(required = false, defaultValue = "false") boolean payForFreedom);
}
