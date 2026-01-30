package com.dirty.code.repository.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "avatar_special_action")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class AvatarSpecialAction extends BaseModel {

    @Id
    @Column(name = "avatar_id")
    private UUID avatarId;

    @OneToOne
    @MapsId
    @JoinColumn(name = "avatar_id")
    private Avatar avatar;

    @Column(name = "dr_strange_visible")
    @Builder.Default
    private Boolean drStrangeVisible = false;

    @Column(name = "dr_strange_last_update")
    private LocalDateTime drStrangeLastUpdate;

    public void checkAndHandleDrStrangeVisibility() {
        LocalDateTime now = LocalDateTime.now();
        if (drStrangeLastUpdate == null || now.isAfter(drStrangeLastUpdate.plusMinutes(10))) {
            this.drStrangeVisible = Math.random() < 0.25;
            this.drStrangeLastUpdate = now;
        }
    }
}
