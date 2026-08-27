package com.baitussalam.aifundraiser.common;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import java.util.Arrays;
import java.util.Optional;

@Getter
@RequiredArgsConstructor
public enum Country {
    PAKISTAN("pakistan", "PK", 30.37, 69.34),
    SYRIA("syria", "SY", 34.80, 38.99),
    PALESTINE("palestine", "PS", 31.95, 35.23);

    private final String name;
    private final String code;
    private final double lat;
    private final double lon;

    public static Optional<Country> fromName(String name){
        return Arrays.stream(values()).filter(c->c.name.equalsIgnoreCase(name.trim())).findFirst();
    }
}