package com.dirty.code.utils;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.Map;
import java.util.Random;

import com.dirty.code.repository.model.Attribute;
import com.dirty.code.repository.model.TimeoutType;
import java.time.LocalDateTime;

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

    public static final int MAX_LIFE = 100;
    public static final int MAX_STAMINA = 100;
    public static final double HIGH_RISK_THRESHOLD = 0.5;
    public static final int LOW_RISK_MULTIPLIER = 1;
    public static final int HIGH_RISK_MULTIPLIER = 3;
    public static final int WANTED_LEVEL_INCREMENT_BASE = 20;
    public static final int JAIL_WANTED_LEVEL_THRESHOLD = 100;
    public static final int BASE_TIMEOUT_MINUTES = 5;
    public static final long HOSPITAL_COST_PER_LEVEL_BASE = 500L;
    public static final long JAIL_COST_PER_LEVEL_BASE = 1000L;
    public static final double PRICE_INCREASE_FACTOR = 1.5;
    public static final int TEMPORARY_STATS_COOLDOWN_HOURS = 24;
    public static final int PERMANENT_STAT_INCREMENT = 1;

    public static int riskMultiplier(double failureChance) {
        return failureChance > HIGH_RISK_THRESHOLD ? HIGH_RISK_MULTIPLIER : LOW_RISK_MULTIPLIER;
    }

    public static int wantedLevelIncrement(double failureChance) {
        return WANTED_LEVEL_INCREMENT_BASE * riskMultiplier(failureChance);
    }

    public static int timeoutMinutes(int level, int multiplier) {
        return BASE_TIMEOUT_MINUTES * Math.max(1, level) * multiplier;
    }

    public static BigDecimal timeoutCost(TimeoutType type, int level, int multiplier) {
        long baseCost = (type == TimeoutType.HOSPITAL ? HOSPITAL_COST_PER_LEVEL_BASE : JAIL_COST_PER_LEVEL_BASE);
        return BigDecimal.valueOf(baseCost * Math.max(1, level) * multiplier);
    }

    public static int clampLife(int life) {
        return Math.min(MAX_LIFE, Math.max(0, life));
    }

    public static int clampStamina(int stamina) {
        return Math.min(MAX_STAMINA, Math.max(0, stamina));
    }

    public static BigDecimal clampMoney(BigDecimal money) {
        return money.compareTo(BigDecimal.ZERO) < 0 ? BigDecimal.ZERO : money;
    }

    public static BigDecimal priceIncrease(BigDecimal currentPrice) {
        return currentPrice.abs().multiply(BigDecimal.valueOf(PRICE_INCREASE_FACTOR)).negate();
    }

    public static LocalDateTime temporaryStatsCooldown(LocalDateTime now) {
        return now.plusHours(TEMPORARY_STATS_COOLDOWN_HOURS);
    }

    public static int permanentStatIncrement(Integer current) {
        return (current != null ? current : 0) + PERMANENT_STAT_INCREMENT;
    }
}
