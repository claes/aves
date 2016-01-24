/*
 * Copyright (c) Claes Holmerson 2013, 2015. The program is licensed under GNU GPL v3. See LICENSE.txt for details.
 */

package se.eliga.aves;

import android.app.Application;
import android.app.ProgressDialog;
import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import org.acra.ACRA;
import org.acra.ReportingInteractionMode;
import org.acra.annotation.ReportsCrashes;

import java.io.File;
import java.io.IOException;
import java.util.Observable;
import java.util.Observer;

import se.eliga.aves.model.Bird;
import se.eliga.aves.model.DatabaseHandler;

/**
 * Created by Claes on 2013-07-19.
 */
@ReportsCrashes(
        mailTo = "fagelappen@gmail.com",
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
        dbHandler = new DatabaseHandler();
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

    }

	public DatabaseHandler getDbHandler(Context context) {
        return dbHandler;
    }

    public Location getLocation() {
        return location;
    }

    /*
    public void initializeDatabase(MainActivity context) {
        try {
            dbHandler = new DatabaseHandler();
            DbProgressTask dbProgressTask = new DbProgressTask(context);
            dbProgressTask.execute(context);
            dbProgressTask.get();
        } catch (Exception e) {
            Log.e(TAG, "Exception while creating database handler", e);
        }
	}

    class DbProgressTask extends AsyncTask<Context, Void, Void> implements Observer {


        private MainActivity context;

        DbProgressTask(MainActivity context) {
            this.context = context;
        }
        @Override
        public void update(Observable observable, Object o) {
            publishProgress();
        }

        @Override
        protected void onPreExecute() {
            //show your dialog here
            context.progressDialog = new ProgressDialog(getApplicationContext());
            context.progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            context.progressDialog.setIndeterminate(false);
            context.progressDialog.setProgress(0);
            context.progressDialog.setMax(100);
            context.progressDialog.show(this.context, "Initierar data. Var god vänta.", "");
        }

        @Override
        protected Void doInBackground(Context... params) {
            //update your DB - it will run in a different thread
            dbHandler.setInitObserver(this);
            try {
                dbHandler.downloadAndInitializeDatabase(params[0]);
            } catch (IOException e) {
                Log.e(TAG, "Exception while downloading / initializing database", e);
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            context.progressDialog.setMessage(dbHandler.getInitStatus().getStep());
            context.progressDialog.setProgress(dbHandler.getInitStatus().getProgress());
        }

        @Override
        protected void onPostExecute(Void result) {
            //hide your dialog here
            context.progressDialog.dismiss();
        }


    }
*/

}
