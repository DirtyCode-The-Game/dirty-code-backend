package com.dirty.code.service;

import com.dirty.code.repository.model.Attribute;
import com.dirty.code.repository.model.Avatar;
import com.dirty.code.repository.model.GameAction;
import com.dirty.code.repository.model.GameActionType;
import com.dirty.code.repository.model.SpecialAction;
import com.dirty.code.repository.model.TimeoutType;
import com.dirty.code.utils.GameFormulas;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class GameActionProcessor {

    private final AvatarTimeoutService timeoutService;

    public double calculateFailureChance(Avatar avatar, GameAction action) {
        GameActionType type = action.getType();
        if (type == GameActionType.TRAINING || type == GameActionType.MARKET || type == GameActionType.HOSPITAL) {
            return 0.0;
        }

        return GameFormulas.calculateFailureChance(
                action.getFailureChance() != null ? action.getFailureChance() : 0.0,
                Map.of(
                        Attribute.STRENGTH, action.getRequiredStrength() != null ? action.getRequiredStrength() : 0,
                        Attribute.INTELLIGENCE, action.getRequiredIntelligence() != null ? action.getRequiredIntelligence() : 0,
                        Attribute.CHARISMA, action.getRequiredCharisma() != null ? action.getRequiredCharisma() : 0,
                        Attribute.STEALTH, action.getRequiredStealth() != null ? action.getRequiredStealth() : 0
                ),
                Map.of(
                        Attribute.STRENGTH, avatar.getEffectiveStrength(),
                        Attribute.INTELLIGENCE, avatar.getEffectiveIntelligence(),
                        Attribute.CHARISMA, avatar.getEffectiveCharisma(),
                        Attribute.STEALTH, avatar.getEffectiveStealth()
                )
        );
    }

    public boolean processActionEffects(Avatar avatar, GameAction action) {
        if (action.getSpecialAction() != null) {
            applySpecialAction(avatar, action);
            return true;
        }

        // Apply Stamina cost/gain
        avatar.setStamina(Math.min(100, Math.max(0, avatar.getStamina() + (action.getStamina() != null ? action.getStamina() : 0))));

        // Apply HP gain (if any)
        if (action.getHp() != null) {
            int hpToAdd = GameFormulas.calculateHpVariation(action.getHp(), action.getHpVariation());
            avatar.setLife(Math.min(100, Math.max(0, avatar.getLife() + hpToAdd)));
        }

        if (timeoutService.checkAndHandleHospitalization(avatar)) {
            log.info("Avatar {} sent to hospital after failure (HP variations)", avatar.getName());
            return false;
        }

        double failureChance = calculateFailureChance(avatar, action);
        if (GameFormulas.isFailure(failureChance)) {
            handleFailure(avatar, action, failureChance);
            return false;
        }

        handleSuccess(avatar, action);
        return true;
    }

    private void applySpecialAction(Avatar avatar, GameAction action) {
        if (action.getSpecialAction() == SpecialAction.CLEAR_TEMPORARY_STATUS) {
            avatar.setTemporaryStrength(0);
            avatar.setTemporaryIntelligence(0);
            avatar.setTemporaryCharisma(0);
            avatar.setTemporaryStealth(0);
            avatar.setStatusCooldown(null);
        }
    }

    private void handleFailure(Avatar avatar, GameAction action, double failureChance) {
        boolean isHighRisk = failureChance > 0.5;
        int multiplier = isHighRisk ? 3 : 1;

        if (action.getLostHpFailure() != null) {
            int hpToLose = action.getLostHpFailure();
            if (action.getLostHpFailureVariation() != null && action.getLostHpFailureVariation() > 0) {
                hpToLose = GameFormulas.calculateXpVariation(hpToLose, action.getLostHpFailureVariation());
            }
            avatar.setLife(Math.min(100, Math.max(0, avatar.getLife() - (hpToLose * multiplier))));
        }

        if (timeoutService.checkAndHandleHospitalization(avatar)) {
            log.info("Avatar {} died during failure and sent to hospital", avatar.getName());
        } else if (Boolean.TRUE.equals(action.getCanBeArrested())) {
            int jailTimeMinutes = 5 * multiplier;
            avatar.setTimeout(LocalDateTime.now().plusMinutes(jailTimeMinutes));
            avatar.setTimeoutType(TimeoutType.JAIL);
            log.info("Avatar {} arrested and sent to jail until {} (High risk: {})", avatar.getName(), avatar.getTimeout(), isHighRisk);
        }
    }

    private void handleSuccess(Avatar avatar, GameAction action) {
        if (action.getMoney() != null) {
            BigDecimal moneyToAdd = GameFormulas.calculateMoneyVariation(action.getMoney(), action.getMoneyVariation());
            BigDecimal newMoney = avatar.getMoney().add(moneyToAdd);
            if (newMoney.compareTo(BigDecimal.ZERO) < 0) {
                newMoney = BigDecimal.ZERO;
            }
            avatar.setMoney(newMoney);
        }

        if (action.getXp() != null) {
            int xpToAdd = GameFormulas.calculateXpVariation(action.getXp(), action.getXpVariation());
            avatar.increaseExperience(xpToAdd);
        }

        applyTemporaryStats(avatar, action);
    }

    private void applyTemporaryStats(Avatar avatar, GameAction action) {
        boolean hasTempStats = false;
        
        if (action.getTemporaryStrength() != null && action.getTemporaryStrength() != 0) {
            avatar.setTemporaryStrength((avatar.getTemporaryStrength() != null ? avatar.getTemporaryStrength() : 0) + action.getTemporaryStrength());
            hasTempStats = true;
        }
        if (action.getTemporaryIntelligence() != null && action.getTemporaryIntelligence() != 0) {
            avatar.setTemporaryIntelligence((avatar.getTemporaryIntelligence() != null ? avatar.getTemporaryIntelligence() : 0) + action.getTemporaryIntelligence());
            hasTempStats = true;
        }
        if (action.getTemporaryCharisma() != null && action.getTemporaryCharisma() != 0) {
            avatar.setTemporaryCharisma((avatar.getTemporaryCharisma() != null ? avatar.getTemporaryCharisma() : 0) + action.getTemporaryCharisma());
            hasTempStats = true;
        }
        if (action.getTemporaryStealth() != null && action.getTemporaryStealth() != 0) {
            avatar.setTemporaryStealth((avatar.getTemporaryStealth() != null ? avatar.getTemporaryStealth() : 0) + action.getTemporaryStealth());
            hasTempStats = true;
        }

        if (hasTempStats) {
            avatar.setStatusCooldown(LocalDateTime.now().plusHours(24));
            log.info("Applied temporary stats to avatar {} from action {}. Cooldown set to {}", 
                    avatar.getName(), action.getTitle(), avatar.getStatusCooldown());
        }
    }
}
