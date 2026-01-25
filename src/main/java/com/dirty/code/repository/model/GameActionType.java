package com.dirty.code.repository.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum GameActionType {
    HACKING("HACKING"),
    TRAINING("TRAINING"),
    WORK("WORK"),
    MARKET("MARKET"),
    HOSPITAL("HOSPITAL"),
    JAIL("JAIL");

    private final String value;

    GameActionType(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static GameActionType fromValue(String value) {
        for (GameActionType type : GameActionType.values()) {
            if (type.value.equalsIgnoreCase(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown GameActionType: " + value);
    }
}
