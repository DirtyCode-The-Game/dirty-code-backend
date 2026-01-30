package com.dirty.code.utils;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.Map;
import java.util.Random;

import com.dirty.code.repository.model.Attribute;

public class GameFormulas {

    private static final Random RANDOM = new Random();

    public static BigInteger requiredExperienceForLevel(long level) {
        if (level <= 0) {
            return new BigInteger("99999999");
        }

        BigDecimal exponentialBase = BigDecimal.valueOf(120);
        BigDecimal growthRate = new BigDecimal("1.18");
        BigDecimal quadraticFactor = BigDecimal.valueOf(25);

        MathContext mathContext = new MathContext(80, RoundingMode.HALF_UP);

        BigDecimal levelAsBigDecimal = BigDecimal.valueOf(level);
        BigDecimal levelSquared = levelAsBigDecimal.multiply(levelAsBigDecimal, mathContext);

        BigDecimal exponentialTerm = exponentialBase.multiply(pow(growthRate, level, mathContext), mathContext);
        BigDecimal quadraticTerm = quadraticFactor.multiply(levelSquared, mathContext);

        BigDecimal adjustedRequiredExperience = BigDecimal.valueOf(880)
                .add(exponentialTerm, mathContext)
                .add(quadraticTerm, mathContext);

        return adjustedRequiredExperience.setScale(0, RoundingMode.HALF_UP).toBigInteger();
    }

    private static BigDecimal pow(BigDecimal baseValue, long exponent, MathContext mathContext) {
        if (exponent == 0L) {
            return BigDecimal.ONE;
        }

        BigDecimal result = BigDecimal.ONE;
        BigDecimal factor = baseValue;
        long remainingExponent = exponent;

        while (remainingExponent > 0L) {
            if ((remainingExponent & 1L) == 1L) {
                result = result.multiply(factor, mathContext);
            }
            remainingExponent >>= 1;
            if (remainingExponent > 0L) {
                factor = factor.multiply(factor, mathContext);
            }
        }

        return result;
    }


    public static BigDecimal calculateMoneyVariation(BigDecimal baseAmount, Double variation) {
        if (variation == null || variation <= 0) {
            return baseAmount;
        }
        double randomFactor = (RANDOM.nextDouble() * 2 - 1); // random(-1, 1)
        BigDecimal variationAmount = baseAmount.multiply(BigDecimal.valueOf(randomFactor * variation));
        return baseAmount.add(variationAmount);
    }

    public static BigInteger calculateXpVariation(BigInteger baseAmount, Double variation) {
        if (variation == null || variation <= 0) {
            return baseAmount;
        }
        double randomFactor = (RANDOM.nextDouble() * 2 - 1); // random(-1, 1)
        BigDecimal baseAsDecimal = new BigDecimal(baseAmount);
        BigDecimal variationAmount = baseAsDecimal.multiply(BigDecimal.valueOf(randomFactor * variation));
        return baseAsDecimal.add(variationAmount).setScale(0, RoundingMode.HALF_UP).toBigInteger();
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

            if (actual < 0) {
                chance += 5.0; // Soma 5% de risco para cada status negativo
            }
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
