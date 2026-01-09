package com.dirty.code.service;

import com.dirty.code.controller.GameActionController;
import com.dirty.code.dto.ActionResultDTO;
import com.dirty.code.dto.AvatarResponseDTO;
import com.dirty.code.dto.GameActionDTO;
import com.dirty.code.exception.BusinessException;
import com.dirty.code.exception.ResourceNotFoundException;
import com.dirty.code.repository.AvatarRepository;
import com.dirty.code.repository.GameActionRepository;
import com.dirty.code.repository.model.Avatar;
import com.dirty.code.repository.model.GameAction;
import com.dirty.code.utils.GameFormulas;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class GameActionService implements GameActionController {

    private final GameActionRepository gameActionRepository;
    private final AvatarRepository avatarRepository;

    @Override
    public List<GameActionDTO> getActionsByType(String type) {
        return gameActionRepository.findByType(type).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public ActionResultDTO performAction(String uid, UUID actionId) {
        Avatar avatar = avatarRepository.findByUserFirebaseUidAndActiveTrue(uid)
                .orElseThrow(() -> new ResourceNotFoundException("Active avatar not found for user: " + uid));

        GameAction action = gameActionRepository.findById(actionId)
                .orElseThrow(() -> new ResourceNotFoundException("Action not found with ID: " + actionId));

        if (action.getStamina() < 0 && avatar.getStamina() < Math.abs(action.getStamina())) {
            throw new BusinessException("Not enough stamina to perform this action.");
        }

        avatar.setStamina(Math.min(100, Math.max(0, avatar.getStamina() + action.getStamina())));

        double failureChance = GameFormulas.calculateFailureChance(
                action.getRequiredStrength() != null ? action.getRequiredStrength() : 0,
                action.getRequiredIntelligence() != null ? action.getRequiredIntelligence() : 0,
                action.getRequiredCharisma() != null ? action.getRequiredCharisma() : 0,
                action.getRequiredStealth() != null ? action.getRequiredStealth() : 0,
                avatar.getStrength() != null ? avatar.getStrength() : 0,
                avatar.getIntelligence() != null ? avatar.getIntelligence() : 0,
                avatar.getCharisma() != null ? avatar.getCharisma() : 0,
                avatar.getStealth() != null ? avatar.getStealth() : 0
        );

        if (GameFormulas.isFailure(failureChance)) {
            if (action.getLostHpFailure() != null) {
                int hpToLose = action.getLostHpFailure();
                if (action.getLostHpFailureVariation() != null && action.getLostHpFailureVariation() > 0) {
                    hpToLose = GameFormulas.calculateXpVariation(hpToLose, action.getLostHpFailureVariation());
                }
                avatar.setLife(Math.max(0, avatar.getLife() - hpToLose));
            }
            // If failed, we don't apply rewards or attribute gains, only stamina was spent
            Avatar updatedAvatar = avatarRepository.save(avatar);
            return ActionResultDTO.builder()
                    .success(false)
                    .avatar(AvatarResponseDTO.fromAvatar(updatedAvatar))
                    .build();
        }

        if (action.getMoney() != null) {
            BigDecimal moneyToAdd = GameFormulas.calculateMoneyVariation(action.getMoney(), action.getMoneyVariation());
            avatar.setMoney(avatar.getMoney().add(moneyToAdd));
        }

        if (action.getXp() != null) {
            int xpToAdd = GameFormulas.calculateXpVariation(action.getXp(), action.getXpVariation());
            avatar.setExperience(avatar.getExperience() + xpToAdd);
            // TODO: Implement leveling logic
        }

        // Apply attributes if provided
        if (action.getRequiredStrength() != null) {
            avatar.setStrength(avatar.getStrength() + action.getRequiredStrength());
        }
        
        if (action.getRequiredIntelligence() != null) {
            avatar.setIntelligence(avatar.getIntelligence() + action.getRequiredIntelligence());
        }

        if (action.getRequiredCharisma() != null) {
            avatar.setCharisma(avatar.getCharisma() + action.getRequiredCharisma());
        }

        if (action.getRequiredStealth() != null) {
            avatar.setStealth(avatar.getStealth() + action.getRequiredStealth());
        }

        Avatar updatedAvatar = avatarRepository.save(avatar);
        return ActionResultDTO.builder()
                .success(true)
                .avatar(AvatarResponseDTO.fromAvatar(updatedAvatar))
                .build();
    }

    private GameActionDTO convertToDTO(GameAction action) {
        return GameActionDTO.builder()
                .id(action.getId())
                .type(action.getType())
                .title(action.getTitle())
                .description(action.getDescription())
                .stamina(action.getStamina())
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
}
