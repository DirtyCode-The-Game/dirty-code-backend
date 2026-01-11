package com.dirty.code.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dirty.code.controller.GameActionController;
import com.dirty.code.dto.ActionResultDTO;
import com.dirty.code.dto.AvatarResponseDTO;
import com.dirty.code.dto.GameActionDTO;
import com.dirty.code.exception.BusinessException;
import com.dirty.code.exception.ResourceNotFoundException;
import com.dirty.code.repository.AvatarRepository;
import com.dirty.code.repository.GameActionRepository;
import com.dirty.code.repository.model.Attribute;
import com.dirty.code.repository.model.Avatar;
import com.dirty.code.repository.model.GameAction;
import com.dirty.code.utils.GameFormulas;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class GameActionService implements GameActionController {

    private final GameActionRepository gameActionRepository;
    private final AvatarRepository avatarRepository;

    @Override
    public List<GameActionDTO> getActionsByType(String uid, String type) {
        Avatar avatar = avatarRepository.findByUserFirebaseUidAndActiveTrue(uid)
                .orElseThrow(() -> new ResourceNotFoundException("Active avatar not found for user: " + uid));

        return gameActionRepository.findByType(type).stream()
                .map(action -> {
                    GameActionDTO dto = convertToDTO(action);
                    double failureChance = GameFormulas.calculateFailureChance(
                            action.getFailureChance() != null ? action.getFailureChance() : 0.0,
                            Map.of(
                                Attribute.STRENGTH, action.getRequiredStrength() != null ? action.getRequiredStrength() : 0,
                                Attribute.INTELLIGENCE, action.getRequiredIntelligence() != null ? action.getRequiredIntelligence() : 0,
                                Attribute.CHARISMA, action.getRequiredCharisma() != null ? action.getRequiredCharisma() : 0,
                                Attribute.STEALTH, action.getRequiredStealth() != null ? action.getRequiredStealth() : 0
                            ),
                            Map.of(
                                Attribute.STRENGTH, avatar.getStrength() != null ? avatar.getStrength() : 0,
                                Attribute.INTELLIGENCE, avatar.getIntelligence() != null ? avatar.getIntelligence() : 0,
                                Attribute.CHARISMA, avatar.getCharisma() != null ? avatar.getCharisma() : 0,
                                Attribute.STEALTH, avatar.getStealth() != null ? avatar.getStealth() : 0
                            )
                    );
                    dto.setFailureChance(failureChance);
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public ActionResultDTO performAction(String uid, UUID actionId, Integer times) {
        Avatar avatar = avatarRepository.findByUserFirebaseUidAndActiveTrue(uid)
                .orElseThrow(() -> new ResourceNotFoundException("Active avatar not found for user: " + uid));

        if (avatar.getTimeout() != null) {
            if (LocalDateTime.now().isBefore(avatar.getTimeout())) {
                throw new BusinessException("You are currently in " + avatar.getTimeoutType() +
                        " until " + avatar.getTimeout() + ". Please wait.");
            } else {
                avatar.setTimeout(null);
                avatar.setTimeoutType(null);
                avatarRepository.save(avatar);
            }
        }

        GameAction action = gameActionRepository.findById(actionId)
                .orElseThrow(() -> new ResourceNotFoundException("Action not found with ID: " + actionId));

        int executionCount = 0;
        boolean overallSuccess = true;

        for (int i = 0; i < (times != null ? times : 1); i++) {
            if (action.getStamina() < 0 && avatar.getStamina() < Math.abs(action.getStamina())) {
                if (i == 0) {
                    throw new BusinessException("Not enough stamina to perform this action.");
                }
                break;
            }

            executionCount++;
            avatar.setStamina(Math.min(100, Math.max(0, avatar.getStamina() + action.getStamina())));

            if ("work".equalsIgnoreCase(action.getType())) {
                avatar.setWork((avatar.getWork() != null ? avatar.getWork() : 0) + 1);
            } else if ("hacking".equalsIgnoreCase(action.getType())) {
                avatar.setHacking((avatar.getHacking() != null ? avatar.getHacking() : 0) + 1);
            }

            if (action.getHp() != null) {
                int hpToAdd = GameFormulas.calculateHpVariation(action.getHp(), action.getHpVariation());
                avatar.setLife(Math.min(100, Math.max(0, avatar.getLife() + hpToAdd)));
            }

            double failureChance = GameFormulas.calculateFailureChance(
                    action.getFailureChance() != null ? action.getFailureChance() : 0.0,
                    Map.of(
                            Attribute.STRENGTH, action.getRequiredStrength() != null ? action.getRequiredStrength() : 0,
                            Attribute.INTELLIGENCE, action.getRequiredIntelligence() != null ? action.getRequiredIntelligence() : 0,
                            Attribute.CHARISMA, action.getRequiredCharisma() != null ? action.getRequiredCharisma() : 0,
                            Attribute.STEALTH, action.getRequiredStealth() != null ? action.getRequiredStealth() : 0
                    ),
                    Map.of(
                            Attribute.STRENGTH, avatar.getStrength() != null ? avatar.getStrength() : 0,
                            Attribute.INTELLIGENCE, avatar.getIntelligence() != null ? avatar.getIntelligence() : 0,
                            Attribute.CHARISMA, avatar.getCharisma() != null ? avatar.getCharisma() : 0,
                            Attribute.STEALTH, avatar.getStealth() != null ? avatar.getStealth() : 0
                    )
            );

            if (GameFormulas.isFailure(failureChance)) {
                overallSuccess = false;
                if (action.getLostHpFailure() != null) {
                    int hpToLose = action.getLostHpFailure();
                    if (action.getLostHpFailureVariation() != null && action.getLostHpFailureVariation() > 0) {
                        hpToLose = GameFormulas.calculateXpVariation(hpToLose, action.getLostHpFailureVariation());
                    }
                    avatar.setLife(Math.min(100, Math.max(0, avatar.getLife() - hpToLose)));
                }

                if (Boolean.TRUE.equals(action.getCanBeArrested())) {
                    avatar.setTimeout(LocalDateTime.now().plusMinutes(5));
                    avatar.setTimeoutType("JAIL");
                    log.info("Avatar {} arrested and sent to jail until {}", avatar.getName(), avatar.getTimeout());
                }
                break;
            }

            if (action.getMoney() != null) {
                BigDecimal moneyToAdd = GameFormulas.calculateMoneyVariation(action.getMoney(), action.getMoneyVariation());
                avatar.setMoney(avatar.getMoney().add(moneyToAdd));
            }

            if (action.getXp() != null) {
                int xpToAdd = GameFormulas.calculateXpVariation(action.getXp(), action.getXpVariation());
                avatar.increaseExperience(xpToAdd);
            }

            if (avatar.getLife() <= 0) {
                avatar.setTimeout(LocalDateTime.now().plusMinutes(5));
                avatar.setTimeoutType("HOSPITAL");
                log.info("Avatar {} sent to hospital until {}", avatar.getName(), avatar.getTimeout());
                break;
            }
        }

        Avatar updatedAvatar = avatarRepository.save(avatar);
        return ActionResultDTO.builder()
                .success(overallSuccess)
                .avatar(AvatarResponseDTO.fromAvatar(updatedAvatar))
                .timesExecuted(executionCount)
                .build();
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
                .build();
    }

    @Override
    @Transactional
    public ActionResultDTO leaveTimeout(String uid, boolean payForFreedom) {
        log.info("Avatar attempting to leave timeout for user UID: {}, payForFreedom: {}", uid, payForFreedom);

        Avatar avatar = avatarRepository.findByUserFirebaseUidAndActiveTrue(uid)
                .orElseThrow(() -> new ResourceNotFoundException("Active avatar not found for user UID: " + uid));

        if (avatar.getTimeout() == null || avatar.getTimeoutType() == null) {
            throw new BusinessException("Avatar is not in timeout");
        }

        String timeoutType = avatar.getTimeoutType();
        log.info("Avatar {} is in {} timeout until {}", avatar.getName(), timeoutType, avatar.getTimeout());

        boolean timeoutExpired = LocalDateTime.now().isAfter(avatar.getTimeout());

        if (payForFreedom && !timeoutExpired) {
            BigDecimal freedomCost = BigDecimal.valueOf(500).multiply(BigDecimal.valueOf(Math.max(1, avatar.getLevel())));

            if (avatar.getMoney().compareTo(freedomCost) < 0) {
                String errorMsg = String.format("Not enough money to buy freedom. Required: %s, Available: %s",
                        freedomCost, avatar.getMoney());
                log.warn(errorMsg);
                throw new BusinessException(errorMsg);
            }
            avatar.setMoney(avatar.getMoney().subtract(freedomCost));
            log.info("Avatar {} bought freedom from {} for {}", avatar.getName(), timeoutType, freedomCost);
        } else if (!payForFreedom && !timeoutExpired) {
            throw new BusinessException("You must wait for the timeout to expire or pay for freedom!");
        }

        avatar.setTimeout(null);
        avatar.setTimeoutType(null);
        avatar.setLife(100);
        avatar.setStamina(100);

        Avatar savedAvatar = avatarRepository.save(avatar);
        log.info("Avatar {} left {} timeout", savedAvatar.getName(), timeoutType);

        return ActionResultDTO.builder()
                .success(true)
                .avatar(AvatarResponseDTO.fromAvatar(savedAvatar))
                .build();
    }
}
