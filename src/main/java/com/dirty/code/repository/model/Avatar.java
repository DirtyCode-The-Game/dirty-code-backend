package com.dirty.code.repository.model;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.UUID;

import com.dirty.code.utils.GameFormulas;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "avatar")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Avatar extends BaseModel {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String name;
    private String picture;
    private String story;

    @Builder.Default
    private Integer level = 0;
    @Builder.Default
    private BigInteger experience = BigInteger.ZERO;
    @Column(name = "total_experience")
    @Builder.Default
    private BigInteger totalExperience = BigInteger.ZERO;

    @Column(name = "next_level_experience")
    @Builder.Default
    private BigInteger nextLevelExperience = BigInteger.valueOf(1000);

    @Builder.Default
    private Integer stamina = 100;
    @Builder.Default
    private Integer life = 100;
    @Builder.Default
    private BigDecimal money = BigDecimal.ZERO;

    @Column(name = "available_points")
    @Builder.Default
    private Integer availablePoints = 0; // Pontos para distribuir

    @Builder.Default
    private Integer intelligence = 0; // Inteligência
    @Builder.Default
    private Integer charisma = 0; // Carisma
    @Builder.Default
    private Integer strength = 0; // Força
    @Builder.Default
    private Integer stealth = 0; // Discrição
    
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

    @Column(name = "status_cooldown")
    private LocalDateTime statusCooldown;

    @OneToOne(mappedBy = "avatar", cascade = jakarta.persistence.CascadeType.ALL)
    private AvatarSpecialAction specialAction;

    public void checkAndResetTemporaryStats() {
        if (statusCooldown != null && LocalDateTime.now().isAfter(statusCooldown)) {
            this.temporaryStrength = 0;
            this.temporaryIntelligence = 0;
            this.temporaryCharisma = 0;
            this.temporaryStealth = 0;
            this.statusCooldown = null;
        }
    }


    public int getEffectiveStrength() {
        checkAndResetTemporaryStats();
        return (strength != null ? strength : 0) + (temporaryStrength != null ? temporaryStrength : 0);
    }

    public int getEffectiveIntelligence() {
        checkAndResetTemporaryStats();
        return (intelligence != null ? intelligence : 0) + (temporaryIntelligence != null ? temporaryIntelligence : 0);
    }

    public int getEffectiveCharisma() {
        checkAndResetTemporaryStats();
        return (charisma != null ? charisma : 0) + (temporaryCharisma != null ? temporaryCharisma : 0);
    }

    public int getEffectiveStealth() {
        checkAndResetTemporaryStats();
        return (stealth != null ? stealth : 0) + (temporaryStealth != null ? temporaryStealth : 0);
    }

    @Builder.Default
    private Integer hacking = 0; // Hacking
    @Builder.Default
    private Integer work = 0; // Work
    
    @Builder.Default
    private Boolean active = true;
    private LocalDateTime timeout; // When the timeout expires (null if not timed out)

    @Enumerated(EnumType.STRING)
    @Column(name = "timeout_type")
    private TimeoutType timeoutType; // Type of timeout: "HOSPITAL" or "JAIL"

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private DirtyUser user;

    @Column(name = "user_id", nullable = false)
    private UUID userId;


    public void increaseExperience(BigInteger experienceToAdd) {
        if (experienceToAdd == null || experienceToAdd.compareTo(BigInteger.ZERO) <= 0) {
            return;
        }

        if (this.totalExperience == null) {
            this.totalExperience = BigInteger.ZERO;
        }

        this.totalExperience = this.totalExperience.add(experienceToAdd);
        this.experience = this.experience.add(experienceToAdd);

        while (this.experience.compareTo(this.nextLevelExperience) >= 0) {
            this.experience = this.experience.subtract(this.nextLevelExperience);
            this.level++;
            this.availablePoints++;
            this.nextLevelExperience = GameFormulas.requiredExperienceForLevel(this.level + 1);
        }
    }
}
