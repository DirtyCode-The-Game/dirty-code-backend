package com.dirty.code.service;

import com.dirty.code.controller.GameActionController;
import com.dirty.code.dto.ActionResultDTO;
import com.dirty.code.dto.AvatarResponseDTO;
import com.dirty.code.dto.GameActionDTO;
import com.dirty.code.exception.BusinessException;
import com.dirty.code.exception.ResourceNotFoundException;
import com.dirty.code.repository.AvatarRepository;
import com.dirty.code.repository.AvatarSpecialActionRepository;
import com.dirty.code.repository.GameActionRepository;
import com.dirty.code.repository.UserRepository;
import com.dirty.code.repository.model.Avatar;
import com.dirty.code.repository.model.AvatarSpecialAction;
import com.dirty.code.repository.model.DirtyUser;
import com.dirty.code.repository.model.GameAction;
import com.dirty.code.repository.model.GameActionType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class GameActionService implements GameActionController {

    private final GameActionRepository gameActionRepository;
    private final UserRepository userRepository;
    private final AvatarRepository avatarRepository;
    private final AvatarTimeoutService timeoutService;
    private final GameActionProcessor actionProcessor;
    private final AvatarSpecialActionRepository specialActionRepository;

    @Override
    public List<GameActionDTO> getActionsByType(String uid, GameActionType type) {
        DirtyUser user = userRepository.findByFirebaseUid(uid)
                .orElseThrow(() -> new ResourceNotFoundException("DirtyUser not found for user: " + uid));
        
        Avatar avatar = avatarRepository.findByUserIdAndActiveTrue(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Active avatar not found for user: " + uid));

        avatar.checkAndResetTemporaryStats();
        handleDrStrangeVisibility(avatar);
        
        return gameActionRepository.findByType(type).stream()
                .map(action -> {
                    GameActionDTO dto = convertToDTO(action);
                    dto.setFailureChance(actionProcessor.calculateFailureChance(avatar, action));
                    
                    if (type == GameActionType.SPECIAL_STATUS_SELLER) {
                        dto.setMoney(actionProcessor.calculateDynamicPrice(avatar, action));
                    }
                    
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public ActionResultDTO performAction(String uid, UUID actionId, Integer times) {
        DirtyUser user = userRepository.findByFirebaseUid(uid)
                .orElseThrow(() -> new ResourceNotFoundException("DirtyUser not found for user: " + uid));

        Avatar avatar = avatarRepository.findByUserIdAndActiveTrue(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Active avatar not found for user: " + uid));

        avatar.checkAndResetTemporaryStats();
        handleDrStrangeVisibility(avatar);
        timeoutService.validateAndHandleTimeout(avatar);

        GameAction action = gameActionRepository.findById(actionId)
                .orElseThrow(() -> new ResourceNotFoundException("Action not found with ID: " + actionId));

        Map<String, Object> initialStats = captureAvatarStats(avatar);

        int executionCount = 0;
        boolean overallSuccess = true;

        for (int i = 0; i < (times != null ? times : 1); i++) {
            if (!canPerformAction(avatar, action, i == 0)) {
                break;
            }

            executionCount++;
            overallSuccess = actionProcessor.processActionEffects(avatar, action);

            if (!overallSuccess || avatar.getTimeout() != null) {
                break;
            }
        }

        Avatar updatedAvatar = avatarRepository.save(avatar);
        Map<String, Object> variations = calculateVariations(initialStats, updatedAvatar);

        return ActionResultDTO.builder()
                .success(overallSuccess)
                .avatar(AvatarResponseDTO.fromAvatar(updatedAvatar))
                .timesExecuted(executionCount)
                .variations(variations)
                .build();
    }

    private boolean canPerformAction(Avatar avatar, GameAction action, boolean firstExecution) {
        BigDecimal actionMoney = action.getMoney();
        if (action.getType() == GameActionType.SPECIAL_STATUS_SELLER) {
            actionMoney = actionProcessor.calculateDynamicPrice(avatar, action);
        }

        if (actionMoney != null && actionMoney.compareTo(BigDecimal.ZERO) < 0) {
            BigDecimal cost = actionMoney.abs();
            if (avatar.getMoney().compareTo(cost) < 0) {
                if (firstExecution) {
                    throw new BusinessException(String.format("Not enough money. Required: %.2f, Available: %.2f", cost, avatar.getMoney()));
                }
                return false;
            }
        }

        if (action.getStamina() < 0 && avatar.getStamina() < Math.abs(action.getStamina())) {
            if (firstExecution) {
                throw new BusinessException("Not enough stamina to perform this action.");
            }
            return false;
        }

        return true;
    }

    private void handleDrStrangeVisibility(Avatar avatar) {
        AvatarSpecialAction specialAction = avatar.getSpecialAction();
        if (specialAction == null) {
            specialAction = AvatarSpecialAction.builder()
                    .avatar(avatar)
                    .drStrangeVisible(false)
                    .build();
            avatar.setSpecialAction(specialAction);
        }
        specialAction.checkAndHandleDrStrangeVisibility();
        specialActionRepository.save(specialAction);
    }

    private Map<String, Object> captureAvatarStats(Avatar avatar) {
        return Map.of(
                "experience", avatar.getExperience(),
                "life", avatar.getLife(),
                "stamina", avatar.getStamina(),
                "money", avatar.getMoney(),
                "temporaryStrength", avatar.getTemporaryStrength() != null ? avatar.getTemporaryStrength() : 0,
                "temporaryIntelligence", avatar.getTemporaryIntelligence() != null ? avatar.getTemporaryIntelligence() : 0,
                "temporaryCharisma", avatar.getTemporaryCharisma() != null ? avatar.getTemporaryCharisma() : 0,
                "temporaryStealth", avatar.getTemporaryStealth() != null ? avatar.getTemporaryStealth() : 0
        );
    }

    private Map<String, Object> calculateVariations(Map<String, Object> initialStats, Avatar updatedAvatar) {
        return Map.of(
                "experience", updatedAvatar.getExperience() - (int) initialStats.get("experience"),
                "life", updatedAvatar.getLife() - (int) initialStats.get("life"),
                "stamina", updatedAvatar.getStamina() - (int) initialStats.get("stamina"),
                "money", updatedAvatar.getMoney().subtract((BigDecimal) initialStats.get("money")),
                "temporaryStrength", (updatedAvatar.getTemporaryStrength() != null ? updatedAvatar.getTemporaryStrength() : 0) - (int) initialStats.get("temporaryStrength"),
                "temporaryIntelligence", (updatedAvatar.getTemporaryIntelligence() != null ? updatedAvatar.getTemporaryIntelligence() : 0) - (int) initialStats.get("temporaryIntelligence"),
                "temporaryCharisma", (updatedAvatar.getTemporaryCharisma() != null ? updatedAvatar.getTemporaryCharisma() : 0) - (int) initialStats.get("temporaryCharisma"),
                "temporaryStealth", (updatedAvatar.getTemporaryStealth() != null ? updatedAvatar.getTemporaryStealth() : 0) - (int) initialStats.get("temporaryStealth")
        );
    }

    private GameActionDTO convertToDTO(GameAction action) {
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

    @Override
    @Transactional
    public ActionResultDTO leaveTimeout(String uid, boolean payForFreedom) {
        log.info("Avatar attempting to leave timeout for user UID: {}, payForFreedom: {}", uid, payForFreedom);

        DirtyUser user = userRepository.findByFirebaseUid(uid)
                .orElseThrow(() -> new ResourceNotFoundException("DirtyUser not found for user: " + uid));

        Avatar avatar = avatarRepository.findByUserIdAndActiveTrue(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Active avatar not found for user: " + uid));

        Avatar savedAvatar = timeoutService.leaveTimeout(avatar, payForFreedom);

        return ActionResultDTO.builder()
                .success(true)
                .avatar(AvatarResponseDTO.fromAvatar(savedAvatar))
                .build();
    }
}
