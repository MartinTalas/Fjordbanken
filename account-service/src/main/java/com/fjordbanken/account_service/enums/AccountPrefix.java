package com.fjordbanken.account_service.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AccountPrefix {
    NORWEGIAN("NO"),
    HUNGARIAN("HU"),
    EUROPEAN("EU"),
    INTERNATIONAL("INT");

    private final String prefix;

    public static AccountPrefix fromString(String code) {
        if (code == null) {
            throw new IllegalArgumentException("Country code cannot be null");
        }
        for (AccountPrefix pref : values()) {
            if (pref.getPrefix().equalsIgnoreCase(code.trim())) {
                return pref;
            }
        }
        throw new IllegalArgumentException("Unsupported country code: " + code);
    }
}
