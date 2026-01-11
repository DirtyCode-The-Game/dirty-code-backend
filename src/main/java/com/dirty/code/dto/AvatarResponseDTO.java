package com.dirty.code.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import com.dirty.code.repository.model.Avatar;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AvatarResponseDTO {
    private UUID id;
    private String name;
    private String picture;
    private String story;

    private Integer level;
    private Integer experience;
    private Integer nextLevelExperience;

    private Integer stamina;
    private Integer life;
    private BigDecimal money;

    private Integer availablePoints; // Pontos para distribuir
    private Integer intelligence; // Inteligência
    private Integer charisma; // Carisma
    private Integer strength; // Força
    private Integer stealth; // Discrição

    private Boolean active;

    private LocalDateTime timeout; // When the timeout expires
    private String timeoutType; // "HOSPITAL" or "JAIL"

    public static AvatarResponseDTO fromAvatar(Avatar avatar) {
        return AvatarResponseDTO.builder()
                .id(avatar.getId())
                .name(avatar.getName())
                .picture(avatar.getPicture())
                .story(avatar.getStory())
                .level(avatar.getLevel())
                .experience(avatar.getExperience())
                .nextLevelExperience(avatar.getNextLevelExperience())

                .stamina(avatar.getStamina())
                .life(avatar.getLife())
                .money(avatar.getMoney())

                .availablePoints(avatar.getAvailablePoints())
                .intelligence(avatar.getIntelligence())
                .charisma(avatar.getCharisma())
                .strength(avatar.getStrength())
                .stealth(avatar.getStealth())

                .active(avatar.getActive())
                .timeout(avatar.getTimeout())
                .timeoutType(avatar.getTimeoutType())
                .build();
    }
}
