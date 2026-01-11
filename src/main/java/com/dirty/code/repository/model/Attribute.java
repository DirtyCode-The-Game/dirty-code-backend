package com.dirty.code.repository.model;

import lombok.Getter;

@Getter
public enum Attribute {
    STRENGTH("FOR"),
    INTELLIGENCE("INT"),
    CHARISMA("CHA"),
    STEALTH("DIS");

    private final String code;

    Attribute(String code) {
        this.code = code;
    }
}
