package com.dirty.code.controller;

import com.dirty.code.dto.UserResponseDTO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/v1/users")
public interface UserController {

    @GetMapping
    List<UserResponseDTO> getAllUsers();
}
