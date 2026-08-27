package com.baitussalam.aifundraiser.worldBankClient;

public enum WorldBankIndicator {


    POPULATION(
            "SP.POP.TOTL",
            "Total Population"
    ),

    POVERTY(
            "SI.POV.NAHC",
            "Population below poverty line"
    ),

    GDP(
            "NY.GDP.MKTP.CD",
            "GDP"
    ),

    UNEMPLOYMENT(
            "SL.UEM.TOTL.ZS",
            "Unemployment Rate"
    ),

    LIFE_EXPECTANCY(
            "SP.DYN.LE00.IN",
            "Life Expectancy"
    ),

    ACCESS_TO_WATER(
            "SH.H2O.BASW.ZS",
            "Access to basic drinking water"
    );


    private final String code;

    private final String description;


    WorldBankIndicator(
            String code,
            String description
    ){
        this.code = code;
        this.description = description;
    }


    public String getCode(){
        return code;
    }


    public String getDescription(){
        return description;
    }
}
