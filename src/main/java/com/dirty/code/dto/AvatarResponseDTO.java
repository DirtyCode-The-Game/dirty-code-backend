package com.dirty.code.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import com.dirty.code.repository.model.Avatar;
import com.dirty.code.repository.model.TimeoutType;

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
    private Integer totalExperience;
    private Integer nextLevelExperience;

    private Integer stamina;
    private Integer life;
    private BigDecimal money;

    private Integer availablePoints; // Pontos para distribuir
    private Integer intelligence; // Inteligência
    private Integer charisma; // Carisma
    private Integer strength; // Força
    private Integer stealth; // Discrição
    private Integer temporaryStrength;
    private Integer temporaryIntelligence;
    private Integer temporaryCharisma;
    private Integer temporaryStealth;
    private LocalDateTime statusCooldown;
    private Integer hacking; // Hacking
    private Integer work; // Work
    private String focus; // "hacking", "work" or "both"

    private Boolean active;

    private LocalDateTime timeout; // When the timeout expires
    private TimeoutType timeoutType; // "HOSPITAL" or "JAIL"

    public static AvatarResponseDTO fromAvatar(Avatar avatar) {
        return AvatarResponseDTO.builder()
                .id(avatar.getId())
                .name(avatar.getName())
                .picture(avatar.getPicture())
                .story(avatar.getStory())
                .level(avatar.getLevel())
                .experience(avatar.getExperience())
                .totalExperience(avatar.getTotalExperience())
                .nextLevelExperience(avatar.getNextLevelExperience())

                .stamina(avatar.getStamina())
                .life(avatar.getLife())
                .money(avatar.getMoney())

                .availablePoints(avatar.getAvailablePoints())
                .intelligence(avatar.getIntelligence())
                .charisma(avatar.getCharisma())
                .strength(avatar.getStrength())
                .stealth(avatar.getStealth())
                .temporaryStrength(avatar.getTemporaryStrength())
                .temporaryIntelligence(avatar.getTemporaryIntelligence())
                .temporaryCharisma(avatar.getTemporaryCharisma())
                .temporaryStealth(avatar.getTemporaryStealth())
                .statusCooldown(avatar.getStatusCooldown())
                .hacking(avatar.getHacking())
                .work(avatar.getWork())
                .focus(calculateFocus(avatar.getWork(), avatar.getHacking()))

                .active(avatar.getActive())
                .timeout(avatar.getTimeout())
                .timeoutType(avatar.getTimeoutType())
                .build();
    }

    private static String calculateFocus(Integer work, Integer hacking) {
        int w = work != null ? work : 0;
        int h = hacking != null ? hacking : 0;
        int diff = w - h;

        if (Math.abs(diff) < 5) {
            return "both";
        }

        return diff > 0 ? "work" : "hacking";
    }
}
