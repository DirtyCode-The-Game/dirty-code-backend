package com.dirty.code.repository.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import com.dirty.code.utils.GameFormulas;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "avatars")
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
    private Integer nextLevelExperience;

    private Integer stamina;
    private Integer life;
    private BigDecimal money;

    private Integer availablePoints; // Pontos para distribuir
    private Integer intelligence; // Inteligência
    private Integer charisma; // Carisma
    private Integer strength; // força
    private Integer stealth; // Discrição

    private Boolean active;

    private LocalDateTime timeout; // When the timeout expires (null if not timed out)
    private String timeoutType; // Type of timeout: "HOSPITAL" or "JAIL"

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public void increaseExperience(int xpToAdd) {
        this.experience += xpToAdd;

        while (this.experience >= this.nextLevelExperience) {
            this.level++;
            this.availablePoints++;
            this.nextLevelExperience = GameFormulas.calculateNextLevelExperience(this.nextLevelExperience);
        }
    }

}
