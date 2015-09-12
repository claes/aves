/*
 * Copyright (c) Claes Holmerson 2013, 2015. The program is licensed under GNU GPL v3. See LICENSE.txt for details.
 */

package se.eliga.aves.maps;

import android.location.Location;
import android.os.AsyncTask;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

import se.eliga.aves.BirdApp;

/**
 * Created by Claes on 2015-06-09.
 */
public class GoToCurrentPositionOperation extends
        AsyncTask<String, Void, String> {

    private WebView view;

    public GoToCurrentPositionOperation(WebView context) {
        this.view = context;
    }

    @Override
    protected String doInBackground(String... params) {
        return null;
    }

    @Override
    protected void onPostExecute(final String result) {
        Location location = ((BirdApp) view.getContext().getApplicationContext()).getLocation();
        if (location != null) {
            view.loadUrl("javascript:setLatLongZoom(" + location.getLatitude() + "," + location.getLongitude() + ",8);");
        }
    }

}
