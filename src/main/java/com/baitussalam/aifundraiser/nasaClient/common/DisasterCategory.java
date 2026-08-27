package com.baitussalam.aifundraiser.nasaClient.common;

public enum DisasterCategory {

    FLOODS("floods"),

    WILDFIRES("wildfires"),

    SEVERE_STORMS("severeStorms"),

    VOLCANOES("volcanoes"),

    DROUGHT("drought"),

    LANDSLIDES("landslides"),

    SEA_AND_LAKE_ICE("seaLakeIce"),

    SNOW("snow"),

    TEMPERATURE_EXTREMES("temperatureExtremes");

    private final String value;

    DisasterCategory(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}