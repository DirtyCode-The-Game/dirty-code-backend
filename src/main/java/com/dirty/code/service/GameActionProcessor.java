package com.dirty.code.service;

import com.dirty.code.repository.AvatarActionPurchaseRepository;
import com.dirty.code.repository.model.Attribute;
import com.dirty.code.repository.model.Avatar;
import com.dirty.code.repository.model.AvatarActionPurchase;
import com.dirty.code.repository.model.GameAction;
import com.dirty.code.repository.model.GameActionType;
import com.dirty.code.repository.model.TimeoutType;
import com.dirty.code.utils.GameFormulas;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class GameActionProcessor {
    private final AvatarTimeoutService timeoutService;
    private final AvatarActionPurchaseRepository purchaseRepository;

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

    public BigDecimal calculateDynamicPrice(Avatar avatar, GameAction action) {
        if (action.getType() != GameActionType.SPECIAL_STATUS_SELLER) {
            return action.getMoney();
        }

        return purchaseRepository.findByAvatarIdAndActionId(avatar.getId(), action.getId())
                .map(AvatarActionPurchase::getCurrentPrice)
                .orElse(action.getMoney());
    }

    public boolean processActionEffects(Avatar avatar, GameAction action) {
        if (action.getSpecialAction() != null) {
            applySpecialAction(avatar, action);
            return true;
        }

        // Apply Stamina cost/gain
        avatar.setStamina(GameFormulas.clampStamina(avatar.getStamina() + (action.getStamina() != null ? action.getStamina() : 0)));

        // Apply HP gain (if any)
        if (action.getHp() != null) {
            int hpToAdd = GameFormulas.calculateHpVariation(action.getHp(), action.getHpVariation());
            avatar.setLife(GameFormulas.clampLife(avatar.getLife() + hpToAdd));
        }

        if (timeoutService.checkAndHandleHospitalization(avatar, 1)) {
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
        if (action.getType() == GameActionType.SPECIAL_STATUS_SELLER) {
            BigDecimal price = calculateDynamicPrice(avatar, action);
            if (price != null) {
                avatar.setMoney(GameFormulas.clampMoney(avatar.getMoney().add(price)));
            }
        }

        switch (action.getSpecialAction()) {
            case CLEAR_TEMPORARY_STATUS:
                avatar.setTemporaryStrength(0);
                avatar.setTemporaryIntelligence(0);
                avatar.setTemporaryCharisma(0);
                avatar.setTemporaryStealth(0);
                avatar.setStatusCooldown(null);
                break;
            case ADD_STRENGTH:
                avatar.setStrength(GameFormulas.permanentStatIncrement(avatar.getStrength()));
                updateActionCost(avatar, action);
                break;
            case ADD_INTELLIGENCE:
                avatar.setIntelligence(GameFormulas.permanentStatIncrement(avatar.getIntelligence()));
                updateActionCost(avatar, action);
                break;
            case ADD_CHARISMA:
                avatar.setCharisma(GameFormulas.permanentStatIncrement(avatar.getCharisma()));
                updateActionCost(avatar, action);
                break;
            case ADD_STEALTH:
                avatar.setStealth(GameFormulas.permanentStatIncrement(avatar.getStealth()));
                updateActionCost(avatar, action);
                break;
            case VOLUNTARY_WORK:
                Integer wanted = avatar.getWantedLevel();
                if (wanted != null) {
                    avatar.setWantedLevel(Math.max(0, wanted - 50));
                }
                avatar.setLife(GameFormulas.clampLife(avatar.getLife() - 50));
                avatar.setStamina(GameFormulas.clampStamina(avatar.getStamina() - 50));
                BigInteger totalExp = avatar.getTotalExperience();
                if (totalExp != null && totalExp.compareTo(BigInteger.ZERO) > 0) {
                    BigInteger loss = totalExp.divide(BigInteger.valueOf(20));
                    avatar.setExperience(avatar.getExperience().subtract(loss));
                    avatar.setTotalExperience(avatar.getTotalExperience().subtract(loss));
                }
                if (timeoutService.checkAndHandleHospitalization(avatar, 1)) {
                    log.info("Avatar {} sent to hospital after voluntary work", avatar.getName());
                }
                log.info("Avatar {} completed voluntary work: wantedLevel -50, HP -50, totalExperience -5%", avatar.getName());
                break;
        }
    }

    private void updateActionCost(Avatar avatar, GameAction action) {
        AvatarActionPurchase purchase = purchaseRepository.findByAvatarIdAndActionId(avatar.getId(), action.getId())
                .orElse(AvatarActionPurchase.builder()
                        .avatar(avatar)
                        .action(action)
                        .purchaseCount(0)
                        .currentPrice(action.getMoney())
                        .build());

        purchase.setPurchaseCount(purchase.getPurchaseCount() + 1);
        
        purchase.setCurrentPrice(GameFormulas.priceIncrease(purchase.getCurrentPrice()));
        
        purchaseRepository.save(purchase);
        log.info("Action {} cost increased for avatar {} to {}", action.getTitle(), avatar.getName(), purchase.getCurrentPrice());
    }

    private void handleFailure(Avatar avatar, GameAction action, double failureChance) {
        int multiplier = GameFormulas.riskMultiplier(failureChance);
        boolean isHighRisk = failureChance > GameFormulas.HIGH_RISK_THRESHOLD;

        if (action.getLostHpFailure() != null) {
            BigInteger hpToLoseBI = action.getLostHpFailure();
            if (action.getLostHpFailureVariation() != null && action.getLostHpFailureVariation() > 0) {
                hpToLoseBI = GameFormulas.calculateXpVariation(hpToLoseBI, action.getLostHpFailureVariation());
            }
            int totalHpToLose = hpToLoseBI.multiply(BigInteger.valueOf(multiplier)).intValue();
            avatar.setLife(GameFormulas.clampLife(avatar.getLife() - totalHpToLose));
        }

        boolean hospitalized = timeoutService.checkAndHandleHospitalization(avatar, multiplier);
        if (hospitalized) {
            log.info("Avatar {} died during failure and sent to hospital", avatar.getName());
        }
        if (Boolean.TRUE.equals(action.getCanBeArrested())) {
            Integer wantedLevel = avatar.getWantedLevel();
            if (wantedLevel == null) {
                wantedLevel = 0;
            }
            wantedLevel += GameFormulas.WANTED_LEVEL_INCREMENT_BASE;
            wantedLevel *= multiplier;
            avatar.setWantedLevel(wantedLevel);
            if (wantedLevel >= GameFormulas.JAIL_WANTED_LEVEL_THRESHOLD && !hospitalized) {
                int effectiveLevel = Math.max(1, (avatar.getLevel() != null ? avatar.getLevel() : 0));
                int jailTimeMinutes = GameFormulas.timeoutMinutes(effectiveLevel, multiplier);
                avatar.setTimeout(LocalDateTime.now().plusMinutes(jailTimeMinutes));
                avatar.setTimeoutType(TimeoutType.JAIL);
                avatar.setTimeoutCost(GameFormulas.timeoutCost(TimeoutType.JAIL, effectiveLevel, multiplier));
                log.info("Avatar {} sent to jail due to wanted level {}, until {} (High risk: {})", avatar.getName(), wantedLevel, avatar.getTimeout(), isHighRisk);
            }
        }
    }

    private void handleSuccess(Avatar avatar, GameAction action) {
        BigDecimal actionMoney = action.getMoney();
        if (action.getType() == GameActionType.SPECIAL_STATUS_SELLER) {
            actionMoney = calculateDynamicPrice(avatar, action);
        }

        if (actionMoney != null) {
            BigDecimal moneyToAdd = GameFormulas.calculateMoneyVariation(actionMoney, action.getMoneyVariation());
            avatar.setMoney(GameFormulas.clampMoney(avatar.getMoney().add(moneyToAdd)));
        }

        if (action.getXp() != null) {
            BigInteger xpToAdd = GameFormulas.calculateXpVariation(action.getXp(), action.getXpVariation());
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
            avatar.setStatusCooldown(GameFormulas.temporaryStatsCooldown(LocalDateTime.now()));
            log.info("Applied temporary stats to avatar {} from action {}. Cooldown set to {}", 
                    avatar.getName(), action.getTitle(), avatar.getStatusCooldown());
        }
    }
}
