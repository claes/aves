/*
 * Copyright (c) Claes Holmerson 2013, 2015. The program is licensed under GNU GPL v3. See LICENSE.txt for details.
 */

package se.eliga.aves;

import android.app.Application;
import android.util.Log;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.io.File;

import se.eliga.aves.model.DatabaseHandler;

/**
 * Created by Claes on 2013-07-19.
 */
public class BirdApp extends Application {

    private static String TAG = BirdApp.class.getName();

    private DatabaseHandler dbHandler;

    @Override
    public void onCreate() {
        enableHttpResponseCache();
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
    
    

}
