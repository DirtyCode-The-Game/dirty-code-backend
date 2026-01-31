package com.dirty.code.dto.converters;

import com.dirty.code.dto.GameActionDTO;

public class GameActionConverter {
    public static GameActionDTO convertToDTO(com.dirty.code.repository.model.GameAction action) {
        return GameActionDTO.builder()
                .id(action.getId())
                .type(action.getType())
                .title(action.getTitle())
                .description(action.getDescription())
                .stamina(action.getStamina())
                .hp(action.getHp())
                .hpVariation(action.getHpVariation())
                .money(action.getMoney())
                .moneyVariation(action.getMoneyVariation())
                .xp(action.getXp())
                .xpVariation(action.getXpVariation())
                .requiredStrength(action.getRequiredStrength())
                .requiredIntelligence(action.getRequiredIntelligence())
                .requiredCharisma(action.getRequiredCharisma())
                .requiredStealth(action.getRequiredStealth())
                .canBeArrested(action.getCanBeArrested())
                .lostHpFailure(action.getLostHpFailure())
                .lostHpFailureVariation(action.getLostHpFailureVariation())
                .textFile(action.getTextFile())
                .actionImage(action.getActionImage())
                .failureChance(action.getFailureChance())
                .recommendedMaxLevel(action.getRecommendedMaxLevel())
                .temporaryStrength(action.getTemporaryStrength())
                .temporaryIntelligence(action.getTemporaryIntelligence())
                .temporaryCharisma(action.getTemporaryCharisma())
                .temporaryStealth(action.getTemporaryStealth())
                .actionCooldown(action.getActionCooldown())
                .specialAction(action.getSpecialAction())
                .build();
    }
}
