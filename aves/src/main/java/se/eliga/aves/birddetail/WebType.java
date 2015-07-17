/*
 * Copyright (c) Claes Holmerson 2013, 2015. The program is licensed under GNU GPL v3. See LICENSE.txt for details.
 */

package se.eliga.aves.birddetail;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Claes on 2015-07-14.
 */
public enum WebType {

    WIKIPEDIA_SV("wikipedia_sv"),
    WIKIPEDIA_EN("wikipedia_en");

    private final String code;
    private static final Map<String, WebType> valuesByCode;

    static {
        valuesByCode = new HashMap<String, WebType>();
        for (WebType t : WebType.values()) {
            valuesByCode.put(t.code, t);
        }
    }

    private WebType(String code) {
        this.code = code;
    }

    public static WebType lookupByCode(String code) {
        return valuesByCode.get(code);
    }

    public String getCode() {
        return code;
    }
}
