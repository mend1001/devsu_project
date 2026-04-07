package com.menditech.bank.account.enums;

import java.util.Arrays;

public enum AccountTypeConfig {
    SAVINGS("SAVINGS", 478_758L),
    CHECKING("CHECKING", 585_545L),
    DEFAULT("DEFAULT", 100_000L);

    private final String code;
    private final long initialNumber;

    AccountTypeConfig(String code, long initialNumber) {
        this.code = code;
        this.initialNumber = initialNumber;
    }

    public static long getInitialNumber(String accountTypeCode) {
        if (accountTypeCode == null) {
            return DEFAULT.initialNumber;
        }

        return Arrays.stream(values())
                .filter(config -> config.code.equalsIgnoreCase(accountTypeCode))
                .mapToLong(config -> config.initialNumber)
                .findFirst()
                .orElse(DEFAULT.initialNumber);
    }
}
