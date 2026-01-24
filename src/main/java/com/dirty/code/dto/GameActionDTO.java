package com.dirty.code.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GameActionDTO {
    private UUID id;
    private String type;
    private String title;
    private String description;
    private Integer stamina;
    private Integer hp;
    private Double hpVariation;
    private BigDecimal money;
    private Double moneyVariation;
    private Integer xp;
    private Double xpVariation;
    private Integer requiredStrength;
    private Integer requiredIntelligence;
    private Integer requiredCharisma;
    private Integer requiredStealth;
    private Boolean canBeArrested;
    private Integer lostHpFailure;
    private Double lostHpFailureVariation;
    private String textFile;
    private String actionImage;
    private Double failureChance;
    private Integer recommendedMaxLevel;
}
