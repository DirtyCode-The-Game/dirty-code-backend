package com.dirty.code.repository.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum TimeoutType {
    HOSPITAL("HOSPITAL"),
    JAIL("JAIL");

    private final String value;

    TimeoutType(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static TimeoutType fromValue(String value) {
        for (TimeoutType type : TimeoutType.values()) {
            if (type.value.equalsIgnoreCase(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown TimeoutType: " + value);
    }
}
