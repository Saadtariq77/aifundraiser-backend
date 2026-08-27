package com.baitussalam.aifundraiser.serpClient.common;

public enum DonationTrendCategory {

    NATURAL_DISASTER(
            "Natural Disaster",
            new String[]{
                    "earthquake",
                    "flood",
                    "flood relief",
                    "earthquake relief",
                    "disaster relief"
            }
    ),

    FOOD_CRISIS(
            "Food Crisis",
            new String[]{
                    "food relief",
                    "free food",
                    "food shortage",
                    "hunger",
                    "ramadan food package",
            }
    ),

    ORPHANS_CHILDREN(
            "Orphans & Children",
            new String[]{
                    "help orphan children",
                    "child sponsorship",
                    "children charity",
                    "kids donation",
                    "support children"
            }
    ),

    HEALTHCARE(
            "Healthcare",
            new String[]{
                    "medical aid",
                    "medical donation",
                    "hospital donation",
                    "free treatment",
                    "cancer treatment donation",
            }
    ),

    WATER_CRISIS(
            "Water Crisis",
            new String[]{
                    "water crisis",
                    "clean water",
                    "water well",
                    "water project",
                    "thirst relief",
            }
    ),

    EDUCATION(
            "Education",
            new String[]{
                    "education donation",
                    "school donation",
                    "student scholarship",
                    "books donation",
                    "free education",

            }
    ),

    POVERTY(
            "Poverty",
            new String[]{
                    "poverty",
                    "poor families",
                    "cash assistance",
                    "charity donation",
                    "support families"
            }
    ),

    REFUGEES(
            "Refugees",
            new String[]{
                    "refugee crisis",
                    "refugee help",
                    "refugee donation",
                    "humanitarian aid",
                    "war refugees"
            }
    ),

    RAMADAN_CAMPAIGN(
            "Ramadan",
            new String[]{
                    "ramadan donation",
                    "zakat",
                    "iftar",
                    "ramadan ration",
                    "sadqah"
            }
    ),

    QURBANI(
            "Qurbani",
            new String[]{
                    "qurbani",
                    "qurbani donation",
                    "eid ul adha",
                    "sacrifice donation",
                    "qurbani online"
            }
    ),

    WINTER_RELIEF(
            "Winter Relief",
            new String[]{
                    "winter relief",
                    "winter donation",
                    "blanket donation",
                    "warm clothes donation",
                    "cold weather help"
            }
    );


    private final String category;
    private final String[] keywords;


    DonationTrendCategory(String category, String[] keywords) {
        this.category = category;
        this.keywords = keywords;
    }


    public String getCategory() {
        return category;
    }

    public String[] getKeywords() {
        return keywords;
    }
}
