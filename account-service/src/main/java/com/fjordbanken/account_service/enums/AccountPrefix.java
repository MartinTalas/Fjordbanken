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
}
