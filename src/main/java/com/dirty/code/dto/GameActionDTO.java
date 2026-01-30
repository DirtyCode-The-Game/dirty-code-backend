package com.dirty.code.dto;

import com.dirty.code.repository.model.GameActionType;
import com.dirty.code.repository.model.SpecialAction;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GameActionDTO {
    private UUID id;
    private GameActionType type;
    private String title;
    private String description;
    private Integer stamina;
    private Integer hp;
    private Double hpVariation;
    private BigDecimal money;
    private Double moneyVariation;
    private BigInteger xp;
    private Double xpVariation;
    private Integer requiredStrength;
    private Integer requiredIntelligence;
    private Integer requiredCharisma;
    private Integer requiredStealth;
    private Boolean canBeArrested;
    private BigInteger lostHpFailure;
    private Double lostHpFailureVariation;
    private String textFile;
    private String actionImage;
    private Double failureChance;
    private Integer recommendedMaxLevel;
    private Integer temporaryStrength;
    private Integer temporaryIntelligence;
    private Integer temporaryCharisma;
    private Integer temporaryStealth;

    private LocalDateTime actionCooldown;
    private SpecialAction specialAction;
}
