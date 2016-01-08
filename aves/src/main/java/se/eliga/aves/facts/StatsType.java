/*
 * Copyright (c) Claes Holmerson 2013, 2015. The program is licensed under GNU GPL v3. See LICENSE.txt for details.
 */

package se.eliga.aves.facts;

import java.util.HashMap;
import java.util.Map;

public enum StatsType {

    //STATS_MONTHLY("M"),
    STATS_WEEKLY("W");

    private final String code;
    private static final Map<String, StatsType> valuesByCode;

    static {
        valuesByCode = new HashMap<String, StatsType>();
        for (StatsType t : StatsType.values()) {
            valuesByCode.put(t.code, t);
        }
    }

    private StatsType(String code) {
        this.code = code;
    }

    public static StatsType lookupByCode(String code) {
        return valuesByCode.get(code);
    }

    public String getCode() {
        return code;
    }
}
