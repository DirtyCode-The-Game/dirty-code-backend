package com.dirty.code.controller;

import com.dirty.code.dto.UserResponseDTO;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/users")
public interface UserController {

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    UserResponseDTO getMe(@AuthenticationPrincipal String uid);
    
}
