package com.dirty.code.service;

import com.dirty.code.controller.ChatController;
import com.dirty.code.dto.ChatMessageDTO;
import com.dirty.code.dto.ChatRequestDTO;
import com.dirty.code.exception.BusinessException;
import com.dirty.code.repository.AvatarRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.security.Principal;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
@Service
@RestController
@RequiredArgsConstructor
public class ChatService implements ChatController {

    private static final int MAX_MESSAGES = 2000;
    private static final long MESSAGE_COOLDOWN_MS = 15000;
    
    private final AvatarRepository avatarRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final ResourceLoader resourceLoader;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final List<ChatMessageDTO> messages = Collections.synchronizedList(new ArrayList<>());
    private final List<String> placeholderNames = new ArrayList<>();
    private final Map<String, String> userPlaceholderNames = new ConcurrentHashMap<>();
    private final Map<String, Long> lastMessageTimestamps = new ConcurrentHashMap<>();
    private final Random random = new Random();

    @PostConstruct
    public void init() {
        try {
            Resource resource = resourceLoader.getResource("classpath:jsons/placeHolderNames.json");
            JsonNode root = objectMapper.readTree(resource.getInputStream());
            JsonNode namesNode = root.get("badDeveloperFunnyNames");
            if (namesNode != null && namesNode.isArray()) {
                for (JsonNode node : namesNode) {
                    placeholderNames.add(node.asText());
                }
            }
        } catch (IOException e) {
            log.error("Failed to load placeholder names", e);
        }
    }

    @Override
    public void sendMessage(Principal principal, ChatRequestDTO request) {
        String uid = principal.getName();

        long now = System.currentTimeMillis();
        Long lastMessageTime = lastMessageTimestamps.get(uid);

        if (lastMessageTime != null && (now - lastMessageTime) < MESSAGE_COOLDOWN_MS) {
            long remaining = (MESSAGE_COOLDOWN_MS - (now - lastMessageTime)) / 1000;
            throw new BusinessException("Aguarde " + remaining + " segundos para enviar outra mensagem.");
        }

        log.info("Sending message from user UID: {}", uid);

        AtomicReference<String> name = new AtomicReference<>();
        avatarRepository.findByUserFirebaseUidAndActiveTrue(uid)
                .ifPresentOrElse(e -> {
                            name.set(e.getName());
                            userPlaceholderNames.remove(uid);
                        },
                        () -> name.set(userPlaceholderNames.computeIfAbsent(uid, _ -> getRandomPlaceholderName())));


        ChatMessageDTO chatMessage = ChatMessageDTO.builder()
                .avatarName(name.get())
                .message(request.getMessage())
                .build();

        addMessage(chatMessage);
        lastMessageTimestamps.put(uid, now);
        messagingTemplate.convertAndSend("/topic/global-messages", chatMessage);
    }

    @Override
    public List<ChatMessageDTO> getInitialMessages() {
        synchronized (messages) {
            return new ArrayList<>(messages);
        }
    }
    
    private String getRandomPlaceholderName() {
        if (placeholderNames.isEmpty()) {
            return "Dev anônimo";
        }
        String selectedName = placeholderNames.get(random.nextInt(placeholderNames.size()));

        boolean exists;
        synchronized (messages) {
            exists = messages.stream()
                    .anyMatch(m -> m.getAvatarName().equals(selectedName));
        }

        if (exists) {
            int randomDigits = random.nextInt(900) + 100;
            return selectedName + "_" + randomDigits;
        }

        return selectedName;
    }

    private void addMessage(ChatMessageDTO message) {
        synchronized (messages) {
            if (messages.size() >= MAX_MESSAGES) {
                messages.removeFirst();
            }
            messages.add(message);
        }
    }
}
