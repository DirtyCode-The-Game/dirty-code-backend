package com.dirty.code.repository.model;

import jakarta.persistence.Column;
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

import java.math.BigDecimal;
import java.util.UUID;

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

    private Integer level;
    private Integer experience;

    private Integer stamina;
    private Integer life;
    private BigDecimal money;

    private Integer availablePoints; // Pontos para distribuir
    private Integer intelligence; // Inteligência
    private Integer charisma; // Carisma
    private Integer streetIntelligence; // Malandragem
    private Integer stealth; // Discrição

    private Boolean active;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

}
