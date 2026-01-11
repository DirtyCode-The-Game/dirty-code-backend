package com.dirty.code.utils;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Random;

import com.dirty.code.repository.model.Attribute;

public class GameFormulas {

    private static final Random RANDOM = new Random();

    private static final int BASE_EXPERIENCE = 100;

    public static int getBaseExperience() {
        return BASE_EXPERIENCE;
    }

    public static int requiredExperienceForLevel(int level) {
        if (level <= 0) {
            return 0;
        }

        double base = 80.0;
        double exponentialBase = 120.0;
        double growthRate = 1.18;
        double quadraticFactor = 20.0;

        double rawRequiredExperience =
                base
                        + (exponentialBase * Math.pow(growthRate, level))
                        + (quadraticFactor * level * level);

        if (rawRequiredExperience >= Integer.MAX_VALUE) {
            return Integer.MAX_VALUE;
        }

        return (int) Math.round(rawRequiredExperience);
    }

    public static BigDecimal calculateMoneyVariation(BigDecimal baseAmount, Double variation) {
        if (variation == null || variation <= 0) {
            return baseAmount;
        }
        double randomFactor = (RANDOM.nextDouble() * 2 - 1); // random(-1, 1)
        BigDecimal variationAmount = baseAmount.multiply(BigDecimal.valueOf(randomFactor * variation));
        return baseAmount.add(variationAmount);
    }

    public static int calculateXpVariation(int baseAmount, Double variation) {
        return defaultVariationCalc(baseAmount, variation);
    }

    public static int calculateHpVariation(int baseAmount, Double variation) {
        return defaultVariationCalc(baseAmount, variation);
    }

    private static int defaultVariationCalc(int baseAmount, Double variation) {
        if (variation == null || variation <= 0) {
            return baseAmount;
        }
        double randomFactor = (RANDOM.nextDouble() * 2 - 1); // random(-1, 1)
        return (int) Math.round(baseAmount + (baseAmount * randomFactor * variation));
    }

    public static double calculateFailureChance(
            double baseFailureChance,
            Map<Attribute, Integer> requiredAttributes,
            Map<Attribute, Integer> avatarAttributes) {

        double chance = baseFailureChance * 100;

        for (Attribute attribute : Attribute.values()) {
            int required = requiredAttributes.getOrDefault(attribute, 0);
            int actual = avatarAttributes.getOrDefault(attribute, 0);
            chance += calculateAttributeImpact(required, actual);
        }

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
