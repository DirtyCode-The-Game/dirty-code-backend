package com.dirty.code.controller;

import com.dirty.code.dto.ChatMessageDTO;
import com.dirty.code.dto.ChatRequestDTO;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/v1/chat")
public interface ChatController {

    @PostMapping("/new-message")
    @ResponseStatus(HttpStatus.CREATED)
    void sendMessage(Principal principal, @RequestBody ChatRequestDTO request);

    @SubscribeMapping("/global-messages")
    List<ChatMessageDTO> getInitialMessages();
}
