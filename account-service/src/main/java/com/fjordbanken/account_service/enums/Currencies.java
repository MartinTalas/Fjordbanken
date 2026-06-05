package com.fjordbanken.account_service.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Currencies {
    NOK("Norwegian Krone", "kr"),
    HUF("Hungarian Forint", "Ft"),
    EUR("Euro", "€"),
    USD("United States Dollar", "$");

    private final String displayName;
    private final String symbol;
}
