package com.dirty.code.service;

import com.dirty.code.controller.GameActionController;
import com.dirty.code.dto.ActionResultDTO;
import com.dirty.code.dto.AvatarResponseDTO;
import com.dirty.code.dto.GameActionDTO;
import com.dirty.code.dto.converters.GameActionConverter;
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
import com.dirty.code.utils.VariationUtils;
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

        Avatar avatar = avatarRepository.findByUserAndActiveTrue(user)
                .orElseThrow(() -> new ResourceNotFoundException("Active avatar not found for user: " + uid));

        avatar.checkAndResetTemporaryStats();
        handleDrStrangeVisibility(avatar);

        return gameActionRepository.findByType(type).stream()
                .map(action -> {
                    GameActionDTO dto = GameActionConverter.convertToDTO(action);
                    dto.setFailureChance(actionProcessor.calculateFailureChance(avatar, action));
                    dto.setMoney(actionProcessor.calculateDynamicPrice(avatar, action));

                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public ActionResultDTO performAction(String uid, UUID actionId, Integer times) {
        DirtyUser user = userRepository.findByFirebaseUid(uid)
                .orElseThrow(() -> new ResourceNotFoundException("DirtyUser not found for user: " + uid));

        Avatar avatar = avatarRepository.findByUserAndActiveTrue(user)
                .orElseThrow(() -> new ResourceNotFoundException("Active avatar not found for user: " + uid));

        avatar.checkAndResetTemporaryStats();
        handleDrStrangeVisibility(avatar);

        GameAction action = gameActionRepository.findById(actionId)
                .orElseThrow(() -> new ResourceNotFoundException("Action not found with ID: " + actionId));

        if (avatar.getTimeoutType() != null && avatar.getTimeout() != null && java.time.LocalDateTime.now().isBefore(avatar.getTimeout())) {
            throw new BusinessException("Não é possível executar ações durante timeout. Apenas ações específicas da prisão/hospital são permitidas.");
        }
        timeoutService.validateAndHandleTimeout(avatar);

        Map<String, Object> initialStats = VariationUtils.captureAvatarStats(avatar);

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
        Map<String, Object> variations = VariationUtils.calculateVariations(initialStats, updatedAvatar);
        variations.put("actionId", action.getId());
        variations.put("nextMoney", actionProcessor.calculateDynamicPrice(updatedAvatar, action));
        variations.put("nextFailureChance", actionProcessor.calculateFailureChance(updatedAvatar, action));

        return ActionResultDTO.builder()
                .success(overallSuccess)
                .avatar(AvatarResponseDTO.fromAvatar(updatedAvatar))
                .timesExecuted(executionCount)
                .variations(variations)
                .build();
    }

    private boolean canPerformAction(Avatar avatar, GameAction action, boolean firstExecution) {
        BigDecimal actionMoney = actionProcessor.calculateDynamicPrice(avatar, action);

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

    @Override
    @Transactional
    public ActionResultDTO leaveTimeout(String uid, boolean payForFreedom) {
        log.info("Avatar attempting to leave timeout for user UID: {}, payForFreedom: {}", uid, payForFreedom);

        DirtyUser user = userRepository.findByFirebaseUid(uid)
                .orElseThrow(() -> new ResourceNotFoundException("DirtyUser not found for user: " + uid));

        Avatar avatar = avatarRepository.findByUserAndActiveTrue(user)
                .orElseThrow(() -> new ResourceNotFoundException("Active avatar not found for user: " + uid));

        Avatar savedAvatar = timeoutService.leaveTimeout(avatar, payForFreedom);

        return ActionResultDTO.builder()
                .success(true)
                .avatar(AvatarResponseDTO.fromAvatar(savedAvatar))
                .build();
    }
}
