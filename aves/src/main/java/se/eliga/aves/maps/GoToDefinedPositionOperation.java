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
public class GoToDefinedPositionOperation extends
        AsyncTask<String, Void, String> {

    private WebView view;
    private double latitude;
    private double longitude;
    private int zoom;

    public GoToDefinedPositionOperation(WebView context, double latitude, double longitude, int zoom) {
        this.view = context;
        this.longitude = longitude;
        this.latitude = latitude;
        this.zoom = zoom;
    }

    @Override
    protected String doInBackground(String... params) {
        return null;
    }

    @Override
    protected void onPostExecute(final String result) {
        view.loadUrl("javascript:setLatLongZoom("+latitude+","+longitude+","+zoom+");");
    }

}
