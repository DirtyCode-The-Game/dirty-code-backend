package com.dirty.code.repository.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "action")
@EqualsAndHashCode(callSuper = true)
public class GameAction extends BaseModel {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private GameActionType type;

    @Column(nullable = false)
    private String title;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Builder.Default
    private Integer stamina = 0;
    
    @Builder.Default
    private Integer hp = 0;
    
    @Column(name = "hp_variation")
    @Builder.Default
    private Double hpVariation = 0.0;

    @Builder.Default
    private BigDecimal money = BigDecimal.ZERO;

    @Column(name = "money_variation")
    @Builder.Default
    private Double moneyVariation = 0.0;

    @Builder.Default
    private BigInteger xp = BigInteger.ZERO;

    @Column(name = "xp_variation")
    @Builder.Default
    private Double xpVariation = 0.0;

    @Column(name = "required_strength")
    @Builder.Default
    private Integer requiredStrength = 0;

    @Column(name = "required_intelligence")
    @Builder.Default
    private Integer requiredIntelligence = 0;

    @Column(name = "required_charisma")
    @Builder.Default
    private Integer requiredCharisma = 0;

    @Column(name = "required_stealth")
    @Builder.Default
    private Integer requiredStealth = 0;

    @Column(name = "can_be_arrested")
    @Builder.Default
    private Boolean canBeArrested = false;

    @Column(name = "lost_hp_failure")
    @Builder.Default
    private BigInteger lostHpFailure = BigInteger.ZERO;

    @Column(name = "lost_hp_failure_variation")
    @Builder.Default
    private Double lostHpFailureVariation = 0.0;

    @Column(name = "text_file")
    private String textFile;

    @Column(name = "action_image")
    private String actionImage;

    @Column(name = "failure_chance")
    @Builder.Default
    private Double failureChance = 0.0;

    @Column(name = "recommended_max_level")
    @Builder.Default
    private Integer recommendedMaxLevel = 1;

    @Column(name = "temporary_strength")
    @Builder.Default
    private Integer temporaryStrength = 0;

    @Column(name = "temporary_intelligence")
    @Builder.Default
    private Integer temporaryIntelligence = 0;

    @Column(name = "temporary_charisma")
    @Builder.Default
    private Integer temporaryCharisma = 0;

    @Column(name = "temporary_stealth")
    @Builder.Default
    private Integer temporaryStealth = 0;

    @Column(name = "action_cooldown")
    private LocalDateTime actionCooldown;

    @Enumerated(EnumType.STRING)
    @Column(name = "special_action")
    private SpecialAction specialAction;
}
