package com.dirty.code.repository.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

@Getter
public enum Attribute {
    STRENGTH("STRENGTH"),
    INTELLIGENCE("INTELLIGENCE"),
    CHARISMA("CHARISMA"),
    STEALTH("STEALTH");

    private final String value;

    Attribute(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static Attribute fromValue(String value) {
        for (Attribute attr : Attribute.values()) {
            if (attr.name().equalsIgnoreCase(value) || attr.value.equalsIgnoreCase(value)) {
                return attr;
            }
        }
        // Fallback para códigos antigos se necessário (FOR, INT, CHA, DIS)
        if ("FOR".equalsIgnoreCase(value)) return STRENGTH;
        if ("INT".equalsIgnoreCase(value)) return INTELLIGENCE;
        if ("CHA".equalsIgnoreCase(value)) return CHARISMA;
        if ("DIS".equalsIgnoreCase(value)) return STEALTH;
        
        throw new IllegalArgumentException("Unknown Attribute: " + value);
    }
}
