package com.dirty.code.utils;

import java.math.BigDecimal;
import java.util.Random;

public class GameFormulas {

    private static final Random RANDOM = new Random();

    public static BigDecimal calculateMoneyVariation(BigDecimal baseAmount, Double variation) {
        if (variation == null || variation <= 0) {
            return baseAmount;
        }
        double randomFactor = (RANDOM.nextDouble() * 2 - 1); // random(-1, 1)
        BigDecimal variationAmount = baseAmount.multiply(BigDecimal.valueOf(randomFactor * variation));
        return baseAmount.add(variationAmount);
    }

    public static int calculateXpVariation(int baseAmount, Double variation) {
        if (variation == null || variation <= 0) {
            return baseAmount;
        }
        double randomFactor = (RANDOM.nextDouble() * 2 - 1); // random(-1, 1)
        return (int) Math.round(baseAmount + (baseAmount * randomFactor * variation));
    }

    public static int calculateHpVariation(int baseAmount, Double variation) {
        if (variation == null || variation <= 0) {
            return baseAmount;
        }
        double randomFactor = (RANDOM.nextDouble() * 2 - 1); // random(-1, 1)
        return (int) Math.round(baseAmount + (baseAmount * randomFactor * variation));
    }

    public static double calculateFailureChance(
            double baseFailureChance,
            int reqStrength, int reqIntelligence, int reqCharisma, int reqStealth,
            int avatarStrength, int avatarIntelligence, int avatarCharisma, int avatarStealth) {

        double chance = baseFailureChance * 100;

        // Strength
        chance += calculateAttributeImpact(reqStrength, avatarStrength);
        // Intelligence
        chance += calculateAttributeImpact(reqIntelligence, avatarIntelligence);
        // Charisma
        chance += calculateAttributeImpact(reqCharisma, avatarCharisma);
        // Stealth
        chance += calculateAttributeImpact(reqStealth, avatarStealth);

        return Math.max(0, Math.min(100, chance)) / 100.0;
    }

    private static double calculateAttributeImpact(int required, int actual) {
        if (required > actual) {
            return (required - actual) * 5.0;
        } else if (actual > required) {
            return (required - actual) * 1.0; // Isso dará um valor negativo, reduzindo a chance
        }
        return 0;
    }

    public static boolean isFailure(double failureChance) {
        return RANDOM.nextDouble() < failureChance;
    }
}
