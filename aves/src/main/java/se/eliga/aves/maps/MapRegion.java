/*
 * Copyright (c) Claes Holmerson 2013, 2015. The program is licensed under GNU GPL v3. See LICENSE.txt for details.
 */

package se.eliga.aves.maps;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Claes on 2015-07-14.
 */
public enum MapRegion {

    SWEDEN("sweden"),
    CURRENT("current"),
    WESTERN_PALEARCTIS("western_palearctis"),
    WORLD("world");

    private final String code;
    private static final Map<String, MapRegion> valuesByCode;

    static {
        valuesByCode = new HashMap<String, MapRegion>();
        for (MapRegion t : MapRegion.values()) {
            valuesByCode.put(t.code, t);
        }
    }

    private MapRegion(String code) {
        this.code = code;
    }

    public static MapRegion lookupByCode(String code) {
        return valuesByCode.get(code);
    }

    public String getCode() {
        return code;
    }
}
