package com.dirty.code.repository.model;

import java.math.BigDecimal;
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

    private Integer level;
    private Integer experience;
    @Column(name = "total_experience")
    private Integer totalExperience;

    @Column(name = "next_level_experience")
    private Integer nextLevelExperience;

    private Integer stamina;
    private Integer life;
    private BigDecimal money;

    @Column(name = "available_points")
    private Integer availablePoints; // Pontos para distribuir

    private Integer intelligence; // Inteligência
    private Integer charisma; // Carisma
    private Integer strength; // Força
    private Integer stealth; // Discrição
    
    @Column(name = "temporary_strength")
    private Integer temporaryStrength;

    @Column(name = "temporary_intelligence")
    private Integer temporaryIntelligence;

    @Column(name = "temporary_charisma")
    private Integer temporaryCharisma;

    @Column(name = "temporary_stealth")
    private Integer temporaryStealth;

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

    private Integer hacking; // Hacking
    private Integer work; // Work
    
    private Boolean active;
    private LocalDateTime timeout; // When the timeout expires (null if not timed out)

    @Enumerated(EnumType.STRING)
    @Column(name = "timeout_type")
    private TimeoutType timeoutType; // Type of timeout: "HOSPITAL" or "JAIL"

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private DirtyUser user;

    @Column(name = "user_id", nullable = false)
    private UUID userId;


    public void increaseExperience(int experienceToAdd) {
        if (experienceToAdd <= 0) {
            return;
        }

        if (this.totalExperience == null) {
            this.totalExperience = 0;
        }

        long updatedTotalExperience = (long) this.totalExperience + (long) experienceToAdd;
        this.totalExperience = (updatedTotalExperience > Integer.MAX_VALUE) ? Integer.MAX_VALUE : (int) updatedTotalExperience;

        long updatedExperience = (long) this.experience + (long) experienceToAdd;
        this.experience = (updatedExperience > Integer.MAX_VALUE) ? Integer.MAX_VALUE : (int) updatedExperience;

        while (this.experience >= this.nextLevelExperience) {
            this.experience -= this.nextLevelExperience;
            this.level++;
            this.availablePoints++;
            this.nextLevelExperience = GameFormulas.requiredExperienceForLevel(this.level + 1);
        }
    }
}
