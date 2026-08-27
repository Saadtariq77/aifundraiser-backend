package com.baitussalam.aifundraiser.whoClient.common;

public enum HealthIndicator {

    CHILD_STUNTING(
            "NUTSTUNTINGPREV",
            "Child malnutrition - Stunting"
    ),

    CHILD_MORTALITY(
            "CHILD_MORTALITY",
            "Child mortality"
    );

    private final String code;
    private final String description;


    HealthIndicator(String code, String description) {
        this.code = code;
        this.description = description;
    }


    public String getCode() {
        return code;
    }
}