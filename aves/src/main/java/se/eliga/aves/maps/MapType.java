/*
 * Copyright (c) Claes Holmerson 2013, 2015. The program is licensed under GNU GPL v3. See LICENSE.txt for details.
 */

package se.eliga.aves.maps;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Claes on 2015-07-14.
 */
public enum MapType {

    OCCURRENCE("occurrence"),
    DISTRIBUTION("distribution");

    private final String code;
    private static final Map<String, MapType> valuesByCode;

    static {
        valuesByCode = new HashMap<String, MapType>();
        for (MapType t : MapType.values()) {
            valuesByCode.put(t.code, t);
        }
    }

    private MapType(String code) {
        this.code = code;
    }

    public static MapType lookupByCode(String code) {
        return valuesByCode.get(code);
    }

    public String getCode() {
        return code;
    }
}
