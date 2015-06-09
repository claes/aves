/*
 * Copyright (c) Claes Holmerson 2013, 2015. The program is licensed under GNU GPL v3. See LICENSE.txt for details.
 */

package se.eliga.aves;

import android.app.Application;
import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import org.acra.ACRA;
import org.acra.ReportingInteractionMode;
import org.acra.annotation.ReportsCrashes;

import java.io.File;

import se.eliga.aves.model.DatabaseHandler;

/**
 * Created by Claes on 2013-07-19.
 */
@ReportsCrashes(
        mailTo = "claespost-birdapp@yahoo.se", // my email here
        mode = ReportingInteractionMode.TOAST,
        resToastText = R.string.crash_toast_text
)
public class BirdApp extends Application {

    private static String TAG = BirdApp.class.getName();

    private DatabaseHandler dbHandler;

    private Location location;

    @Override
    public void onCreate() {
        super.onCreate();
        if (BuildConfig.REPORT_CRASHES) {
            ACRA.init(this);
        }
        enableHttpResponseCache();
        enableLocationAwareness();
    }

    private void enableLocationAwareness() {
        // Acquire a reference to the system Location Manager
        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        // Define a listener that responds to location updates
        LocationListener locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                BirdApp.this.location = location;
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {}

            public void onProviderEnabled(String provider) {}

            public void onProviderDisabled(String provider) {}
        };

        Criteria criteria = new Criteria();
        String provider = locationManager.getBestProvider(criteria, false);
        location = locationManager.getLastKnownLocation(provider);

        // Register the listener with the Location Manager to receive location updates
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 600000, 1000, locationListener);
    }

    private void enableHttpResponseCache() {

        // Create global configuration and initialize ImageLoader with this configuration
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getApplicationContext())
                .discCacheFileCount(500)
                .threadPoolSize(10)
                .build();

        ImageLoader.getInstance().init(config);

        // see http://android-developers.blogspot.com/2011/09/androids-http-clients.html
        final long httpCacheSize = 10 * 1024 * 1024; // 10 MiB
        final File httpCacheDir = new File(getCacheDir(), "http");
        try {
            Class.forName("android.net.http.HttpResponseCache")
                    .getMethod("install", File.class, long.class)
                    .invoke(null, httpCacheDir, httpCacheSize);
        } catch (Exception e) {
            Log.e(TAG, "Exception while instantiating HttpResponseCache", e);
        }

        dbHandler = new DatabaseHandler(this);
    }

	public DatabaseHandler getDbHandler() {
		return dbHandler;
	}

    public Location getLocation() {
        return location;
    }
    

}
