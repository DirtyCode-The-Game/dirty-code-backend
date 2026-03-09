package com.dirty.code.utils;

import com.dirty.code.repository.model.Avatar;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

public class VariationUtils {

    public static Map<String, Object> captureAvatarStats(Avatar avatar) {
        Map<String, Object> stats = new HashMap<>();

        stats.put("experience", avatar.getExperience());
        stats.put("life", avatar.getLife());
        stats.put("stamina", avatar.getStamina());
        stats.put("money", avatar.getMoney());

        stats.put("temporaryStrength", avatar.getTemporaryStrength() != null ? avatar.getTemporaryStrength() : 0);
        stats.put("temporaryIntelligence", avatar.getTemporaryIntelligence() != null ? avatar.getTemporaryIntelligence() : 0);
        stats.put("temporaryCharisma", avatar.getTemporaryCharisma() != null ? avatar.getTemporaryCharisma() : 0);
        stats.put("temporaryStealth", avatar.getTemporaryStealth() != null ? avatar.getTemporaryStealth() : 0);

        return stats;
    }

    public static Map<String, Object> calculateVariations(Map<String, Object> initialStats, Avatar updatedAvatar) {
        Map<String, Object> variations = new HashMap<>();

        variations.put("experience", updatedAvatar.getExperience().subtract((BigInteger) initialStats.get("experience")));
        variations.put("life",updatedAvatar.getLife() - (Integer) initialStats.get("life"));
        variations.put("stamina", updatedAvatar.getStamina() - (Integer) initialStats.get("stamina"));
        variations.put("money", updatedAvatar.getMoney().subtract((BigDecimal) initialStats.get("money")));

        int updatedTemporaryStrength = updatedAvatar.getTemporaryStrength() != null ? updatedAvatar.getTemporaryStrength() : 0;
        int initialTemporaryStrength = (Integer) initialStats.getOrDefault("temporaryStrength", 0);
        variations.put("temporaryStrength", updatedTemporaryStrength - initialTemporaryStrength);

        int updatedTemporaryIntelligence = updatedAvatar.getTemporaryIntelligence() != null ? updatedAvatar.getTemporaryIntelligence() : 0;
        int initialTemporaryIntelligence = (Integer) initialStats.getOrDefault("temporaryIntelligence", 0);
        variations.put("temporaryIntelligence", updatedTemporaryIntelligence - initialTemporaryIntelligence);

        int updatedTemporaryCharisma = updatedAvatar.getTemporaryCharisma() != null ? updatedAvatar.getTemporaryCharisma() : 0;
        int initialTemporaryCharisma = (Integer) initialStats.getOrDefault("temporaryCharisma", 0);
        variations.put("temporaryCharisma", updatedTemporaryCharisma - initialTemporaryCharisma);

        int updatedTemporaryStealth = updatedAvatar.getTemporaryStealth() != null ? updatedAvatar.getTemporaryStealth() : 0;
        int initialTemporaryStealth = (Integer) initialStats.getOrDefault("temporaryStealth", 0);
        variations.put("temporaryStealth", updatedTemporaryStealth - initialTemporaryStealth);

        return variations;
    }
}
