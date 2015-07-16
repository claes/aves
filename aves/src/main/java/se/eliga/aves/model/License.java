/*
 * Copyright (c) Claes Holmerson 2013, 2015. The program is licensed under GNU GPL v3. See LICENSE.txt for details.
 */

package se.eliga.aves.model;

import se.eliga.aves.BuildConfig;

/**
 * Created by Claes on 2015-05-15.
 */
public enum License {
    ALL_RIGHTS_RESERVED("All rights reserved", "0"),
    BY_NC_SA("BY-NC-SA", "1"),
    BY_NC("BY-NC", "2"),
    BY_NC_ND("BY-NC-ND", "3"),
    BY("BY","4"),
    BY_SA("BY-SA", "5"),
    BY_ND("BY-ND", "6"),
    NO_KNOWN("Unknown", "7"),
    US_GOV("US Government", "8");

    private String code;
    private String flickrCode;

    License(String code, String flickrCode) {
        this.code = code;
        this.flickrCode = flickrCode;
    }

    public String getCode() {
        return this.code;
    }

    public static License fromFlickrCode(String flickrCode) {
        if (flickrCode != null) {
            for (License b : License.values()) {
                if (flickrCode.equals(b.flickrCode)) {
                    return b;
                }
            }
        }
        return null;
    }

    public static License fromXenoCantoCode(String xenoCantoCode) {
        String upperCasedCode = xenoCantoCode.toUpperCase();
        if (xenoCantoCode != null) {
            for (License b : License.values()) {
                if (upperCasedCode.equals(b.getCode())) {
                    return b;
                }
            }
        }
        return null;
    }

    public boolean isUsable() {
        if ("release".equals(BuildConfig.BUILD_TYPE)) {
            return (this == BY_NC) || (this == BY_NC_SA) || (this == BY_NC_ND)
                    || (this == BY) || (this == BY_SA) || (this == BY_ND)
                    || (this == NO_KNOWN);
        } else {
            return true;
        }

    }

}
