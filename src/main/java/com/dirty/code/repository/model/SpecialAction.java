package com.dirty.code.repository.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum SpecialAction {
    CLEAR_TEMPORARY_STATUS("CLEAR_TEMPORARY_STATUS");

    private final String value;

    SpecialAction(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static SpecialAction fromValue(String value) {
        for (SpecialAction type : SpecialAction.values()) {
            if (type.value.equalsIgnoreCase(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown SpecialAction: " + value);
    }
}
