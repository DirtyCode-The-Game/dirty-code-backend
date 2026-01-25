package com.dirty.code.repository.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "actions")
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

    private Integer stamina;
    
    private Integer hp;
    
    @Column(name = "hp_variation")
    private Double hpVariation;

    private BigDecimal money;

    @Column(name = "money_variation")
    private Double moneyVariation;

    private Integer xp;

    @Column(name = "xp_variation")
    private Double xpVariation;

    @Column(name = "required_strength")
    private Integer requiredStrength;

    @Column(name = "required_intelligence")
    private Integer requiredIntelligence;

    @Column(name = "required_charisma")
    private Integer requiredCharisma;

    @Column(name = "required_stealth")
    private Integer requiredStealth;

    @Column(name = "can_be_arrested")
    private Boolean canBeArrested;

    @Column(name = "lost_hp_failure")
    private Integer lostHpFailure;

    @Column(name = "lost_hp_failure_variation")
    private Double lostHpFailureVariation;

    @Column(name = "text_file")
    private String textFile;

    @Column(name = "action_image")
    private String actionImage;

    @Column(name = "failure_chance")
    private Double failureChance;

    @Column(name = "recommended_max_level")
    private Integer recommendedMaxLevel;

    @Column(name = "temporary_strength")
    private Integer temporaryStrength;

    @Column(name = "temporary_intelligence")
    private Integer temporaryIntelligence;

    @Column(name = "temporary_charisma")
    private Integer temporaryCharisma;

    @Column(name = "temporary_stealth")
    private Integer temporaryStealth;

    @Column(name = "action_cooldown")
    private LocalDateTime actionCooldown;

    @Enumerated(EnumType.STRING)
    @Column(name = "special_action")
    private SpecialAction specialAction;
}
