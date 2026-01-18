package com.dirty.code.service;

import com.dirty.code.dto.ChatMessageDTO;
import com.dirty.code.repository.AvatarRepository;
import com.dirty.code.repository.UserRepository;
import com.dirty.code.repository.model.Avatar;
import com.dirty.code.repository.model.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
@Service
@RequiredArgsConstructor
public class SimulatedAvatarService {

    private final AvatarRepository avatarRepository;
    private final UserRepository userRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final Random random = new Random();

    @Lazy
    @Autowired
    private ChatService chatService;

    @Value("${firebase.enabled:false}")
    private boolean firebaseEnabled;

    public void initializeSimulatedAvatars() {
        log.info("Initializing simulated avatars...");
        String[] botNames = {"ByteSurfer", "CodeNinja", "GlitchGhost", "NullPointer", "RootOverlord"};
        
        for (int i = 0; i < botNames.length; i++) {
            String name = botNames[i];
            if (!avatarRepository.existsByNameAndActiveTrue(name)) {
                createSimulatedAvatar(name, i + 1);
            }
        }
    }

    private void createSimulatedAvatar(String name, int difficultyMultiplier) {
        String firebaseUid = "bot-uid-" + name.toLowerCase();
        List<String> avatares = List.of("/avatars/avatar_1.png", "/avatars/avatar_2.png", "/avatars/avatar_3.png");
        String randomAvatarPicture = avatares.get(ThreadLocalRandom.current().nextInt(avatares.size()));

        User botUser = userRepository.findByFirebaseUid(firebaseUid)
                .orElseGet(() -> userRepository.save(User.builder()
                        .firebaseUid(firebaseUid)
                        .name(name + " (Bot)")
                        .email(name.toLowerCase() + "@dirtycode.bot")
                        .build()));

        int level = difficultyMultiplier * 5;
        int exp = 0;
        int nextLevelExp = com.dirty.code.utils.GameFormulas.requiredExperienceForLevel(level + 1);

        avatarRepository.save(Avatar.builder()
                .name(name)
                .user(botUser)
                .level(level)
                .experience(exp)
                .totalExperience(level * 1000)
                .nextLevelExperience(nextLevelExp)
                .stamina(100)
                .life(100)
                .money(BigDecimal.valueOf(1000L * difficultyMultiplier))
                .availablePoints(level)
                .intelligence(level * 2)
                .charisma(level)
                .strength(level)
                .stealth(level)
                .active(true)
                .picture(randomAvatarPicture)
                .build());
        
        log.info("Created simulated avatar: {}", name);
    }

    public void addSimulatedInitialMessages() {
        List<Avatar> simulatedAvatars = avatarRepository.findByActiveTrue();
        if (simulatedAvatars.isEmpty()) {
            return;
        }

        String[] sampleMessages = {
                "Alguém conseguiu invadir o mainframe da Gibson hoje?",
                "Muito fácil, deixaram a porta 21 aberta.",
                "Cuidado, os federais estão monitorando os pacotes.",
                "Alguém sabe onde encontrar o manual do 2600?",
                "Hack the planet!",
                "I hope you're ready for an adrenaline rush, because the data's about to flow."
        };

        for (int i = 0; i < Math.min(sampleMessages.length, simulatedAvatars.size()); i++) {
            Avatar avatar = simulatedAvatars.get(i);
            LocalDateTime msgDate = LocalDateTime.now(ZoneId.of("America/Sao_Paulo"));
            
            if (i < 3) {
                msgDate = msgDate.minusDays(2);
            } else if (i == 3) {
                msgDate = msgDate.minusDays(1);
            }
            
            String timestamp = String.format("%02d:%02d", msgDate.getHour(), msgDate.getMinute());
            String fullDate = String.format("%04d-%02d-%02d", msgDate.getYear(), msgDate.getMonthValue(), msgDate.getDayOfMonth());
            
            chatService.addMessage(ChatMessageDTO.builder()
                    .avatarId(avatar.getId().toString())
                    .avatarName(avatar.getName())
                    .message(sampleMessages[i])
                    .timestamp(timestamp)
                    .fullDate(fullDate)
                    .build());
        }
    }

    @Scheduled(fixedDelay = 60000)
    public void sendSimulatedMessage() {
        if (!firebaseEnabled) {
            List<Avatar> simulatedAvatars = avatarRepository.findByActiveTrue().stream()
                    .filter(a -> a.getUser().getFirebaseUid().startsWith("bot-uid-"))
                    .toList();
            
            if (simulatedAvatars.isEmpty()) {
                return;
            }

            Avatar randomAvatar = simulatedAvatars.get(random.nextInt(simulatedAvatars.size()));
            
            String[] simulatedMessages = {
                    "Alguém tem um convite pro tracker privado?",
                    "Como faz pra debugar esse código legado em COBOL?",
                    "Cuidado com o pessoal da TI, estão bloqueando as portas.",
                    "SSH é vida, Telnet nem pensar.",
                    "Alguém me ajuda com esse regex impossível?",
                    "Acabei de subir um commit que quebrou a build, ops.",
                    "Café acabou, o sistema vai cair em 5 minutos."
            };
            String message = simulatedMessages[random.nextInt(simulatedMessages.length)];
            
            LocalDateTime now = LocalDateTime.now(ZoneId.of("America/Sao_Paulo"));
            String timestamp = String.format("%02d:%02d", now.getHour(), now.getMinute());
            String fullDate = String.format("%04d-%02d-%02d", now.getYear(), now.getMonthValue(), now.getDayOfMonth());
            
            ChatMessageDTO simulated = ChatMessageDTO.builder()
                    .avatarId(randomAvatar.getId().toString())
                    .avatarName(randomAvatar.getName())
                    .message(message)
                    .timestamp(timestamp)
                    .fullDate(fullDate)
                    .build();
            
            chatService.addMessage(simulated);
            messagingTemplate.convertAndSend("/topic/global-messages", simulated);
            log.info("Simulated message sent by {}", randomAvatar.getName());
        }
    }
}
