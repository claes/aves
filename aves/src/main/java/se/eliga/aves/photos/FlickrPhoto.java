/*
 * Copyright (c) Claes Holmerson 2013, 2015. The program is licensed under GNU GPL v3. See LICENSE.txt for details.
 */

package se.eliga.aves.photos;

import android.graphics.Bitmap;

public class FlickrPhoto {

    public static final String PX_800 = "c";
    public static final String PX_640 = "z";
    public static final String PX_500 = "-";
    public static final String PX_1024 = "b";
    public static final String SMALL_SQUARE = "s";
    public static final String THUMBNAIL = "t";

    private String id;
    private String title;
    private String ownerName;
    private int mFarm;
    private String mSecret;
    private String mServer;
    private Bitmap image;
    private License license;
    
	public static enum License {
		ALL_RIGHTS_RESERVED("0"),
		BY_NC_SA("1"),
		BY_NC("2"),
		BY_NC_ND("3"),
		BY("4"),
		BY_SA("5"),
		BY_ND("6"),
		NO_KNOWN("7"),
		US_GOV("8");		
		
		 private String text;

		  License(String text) {
		    this.text = text;
		  }

		  public String getText() {
		    return this.text;
		  }

		  public static License fromString(String text) {
		    if (text != null) {
		      for (License b : License.values()) {
		        if (text.equals(b.text)) {
		          return b;
		        }
		      }
		    }
		    return null;
		  }
		  
		public boolean isUsable() {
            return true;
            /*
			return (this == BY_NC) || (this == BY_NC_SA) || (this == BY_NC_ND)
					|| (this == BY) || (this == BY_SA) || (this == BY_ND)
					|| (this == NO_KNOWN);
					*/
		}

	}
    
    public FlickrPhoto(String id, int farm, String secret, String server, String title, String ownerName, License license) {
        this.id = id;
        this.title = title;
        this.ownerName = ownerName;
        this.license = license;
        mFarm = farm;
        mSecret = secret;
        mServer = server;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }

    public String getThumbnailUrl() {
        return createUrl(THUMBNAIL);
    }

    public String getSmallSquareUrl() {
        return createUrl(SMALL_SQUARE);
    }

    public String get1024pxUrl() {
        return createUrl(PX_1024);
    }

    public String get500pxUrl() {
        return createUrl(PX_500);
    }

    public String get640pxUrl() {
        return createUrl(PX_640);
    }

    public String get800pxUrl() {
        return createUrl(PX_800);
    }

    public String createUrl(String sizeSuffix) {
        int farm = mFarm;
        String secret = mSecret;
        String server = mServer;
        String url = String.format("http://farm%d.static.flickr.com/%s/%s_%s_%s.jpg", farm, server,
                id, secret, sizeSuffix);
        return url;
    }

	public License getLicense() {
		return license;
	}

	public void setLicense(License license) {
		this.license = license;
	}

	@Override
	public String toString() {
		return "FlickrPhoto [id=" + id + ", title=" + title + ", ownerName="
				+ ownerName + ", mFarm=" + mFarm + ", mSecret=" + mSecret
				+ ", mServer=" + mServer + ", image=" + image + ", license="
				+ license + "]";
	}
    
    
}
