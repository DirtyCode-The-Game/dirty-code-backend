package com.dirty.code.service;

import com.dirty.code.controller.GameActionController;
import com.dirty.code.dto.AvatarResponseDTO;
import com.dirty.code.dto.GameActionDTO;
import com.dirty.code.exception.BusinessException;
import com.dirty.code.exception.ResourceNotFoundException;
import com.dirty.code.repository.AvatarRepository;
import com.dirty.code.repository.GameActionRepository;
import com.dirty.code.repository.model.Avatar;
import com.dirty.code.repository.model.GameAction;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

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
    public AvatarResponseDTO performAction(String uid, UUID actionId) {
        Avatar avatar = avatarRepository.findByUserFirebaseUidAndActiveTrue(uid)
                .orElseThrow(() -> new ResourceNotFoundException("Active avatar not found for user: " + uid));

        GameAction action = gameActionRepository.findById(actionId)
                .orElseThrow(() -> new ResourceNotFoundException("Action not found with ID: " + actionId));

        if (action.getStamina() < 0 && avatar.getStamina() < Math.abs(action.getStamina())) {
            throw new BusinessException("Not enough stamina to perform this action.");
        }

        avatar.setStamina(Math.min(100, Math.max(0, avatar.getStamina() + action.getStamina())));

        if (action.getMoney() != null) {
            avatar.setMoney(avatar.getMoney().add(action.getMoney()));
        }

        if (action.getXp() != null) {
            avatar.setExperience(avatar.getExperience() + action.getXp());
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
        return AvatarResponseDTO.fromAvatar(updatedAvatar);
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
                .build();
    }
}
