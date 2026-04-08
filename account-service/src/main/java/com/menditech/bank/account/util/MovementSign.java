package com.menditech.bank.account.util;

public enum MovementSign {
    CREDIT((short) 1),
    DEBIT((short) -1);

    private final short value;

    MovementSign(short value) { this.value = value; }

    public short getValue() { return value; }

    public static MovementSign fromSign(short sign) {
        for (MovementSign ms : values()) {
            if (ms.value == sign) return ms;
        }
        throw new IllegalArgumentException("Unknown movement sign: " + sign);
    }
}
