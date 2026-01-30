package com.dirty.code.service;

import com.dirty.code.exception.BusinessException;
import com.dirty.code.repository.AvatarRepository;
import com.dirty.code.repository.model.Avatar;
import com.dirty.code.repository.model.TimeoutType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class AvatarTimeoutService {

    private final AvatarRepository avatarRepository;

    @Transactional
    public void validateAndHandleTimeout(Avatar avatar) {
        if (avatar.getTimeout() == null) {
            return;
        }

        if (LocalDateTime.now().isAfter(avatar.getTimeout())) {
            log.info("Timeout for avatar {} (type: {}) has expired. Clearing.", avatar.getName(), avatar.getTimeoutType());
            clearTimeout(avatar);
            avatarRepository.save(avatar);
        } else {
            throw new BusinessException("You are currently in " + avatar.getTimeoutType() +
                    " until " + avatar.getTimeout() + ". Please wait.");
        }
    }

    /**
     * Silently clears timeout if it has expired, without throwing exceptions.
     * Useful for background checks or user info retrieval.
     */
    @Transactional
    public void processExpiredTimeoutSilently(Avatar avatar) {
        if (avatar.getTimeout() != null && LocalDateTime.now().isAfter(avatar.getTimeout())) {
            log.info("Silently clearing expired timeout for avatar {} (type: {})", avatar.getName(), avatar.getTimeoutType());
            clearTimeout(avatar);
            avatarRepository.save(avatar);
        }
    }

    public boolean checkAndHandleHospitalization(Avatar avatar, int multiplier) {
        if (avatar.getLife() <= 0) {
            int multiplierByLevel = avatar.getLevel() == 0 ? 1 : avatar.getLevel();
            avatar.setTimeout(LocalDateTime.now().plusMinutes(5L * multiplierByLevel));
            avatar.setTimeoutType(TimeoutType.HOSPITAL);
            avatar.setTimeoutCost(BigDecimal.valueOf((500L * multiplierByLevel) * multiplier));
            log.info("Avatar {} HP reached 0. Sent to hospital until {}.", avatar.getName(), avatar.getTimeout());
            return true;
        }
        return false;
    }

    @Transactional
    public Avatar leaveTimeout(Avatar avatar, boolean payForFreedom) {
        if (avatar.getTimeout() == null || avatar.getTimeoutType() == null) {
            throw new BusinessException("Avatar is not in timeout");
        }

        TimeoutType timeoutType = avatar.getTimeoutType();
        boolean timeoutExpired = LocalDateTime.now().isAfter(avatar.getTimeout());

        if (payForFreedom && !timeoutExpired) {
            BigDecimal freedomCost = avatar.getTimeoutCost();

            if (avatar.getMoney().compareTo(freedomCost) < 0) {
                String errorMsg = String.format("Not enough money to buy freedom. Required: %.2f, Available: %.2f",
                        freedomCost, avatar.getMoney());
                log.warn(errorMsg);
                throw new BusinessException(errorMsg);
            }

            BigDecimal newMoney = avatar.getMoney().subtract(freedomCost);
            if (newMoney.compareTo(BigDecimal.ZERO) < 0) {
                newMoney = BigDecimal.ZERO;
            }
            avatar.setMoney(newMoney);
            log.info("Avatar {} bought freedom from {} for {}", avatar.getName(), timeoutType, freedomCost);
        } else if (!payForFreedom && !timeoutExpired) {
            throw new BusinessException("You must wait for the timeout to expire or pay for freedom!");
        }

        clearTimeout(avatar);
        return avatarRepository.save(avatar);
    }

    private void clearTimeout(Avatar avatar) {
        if (avatar.getTimeoutType() == TimeoutType.JAIL) {
            avatar.setWantedLevel(0);
        }
        
        if (avatar.getTimeoutType() == TimeoutType.HOSPITAL) {
            avatar.setLife(1);
        }
        
        avatar.setActive(true);
        avatar.setTimeout(null);
        avatar.setTimeoutType(null);
        avatar.setTimeoutCost(BigDecimal.ZERO);
    }
}
