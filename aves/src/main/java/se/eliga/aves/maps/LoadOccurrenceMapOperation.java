/*
 * Copyright (c) Claes Holmerson 2013, 2015. The program is licensed under GNU GPL v3. See LICENSE.txt for details.
 */

package se.eliga.aves.maps;

import android.os.AsyncTask;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

import se.eliga.aves.model.Bird;

public class LoadOccurrenceMapOperation extends
        AsyncTask<String, Void, String> {

    private WebView view;
    private Bird bird;

    public LoadOccurrenceMapOperation(WebView context, Bird bird) {
        this.view = context;
        this.bird = bird;
    }

    @Override
    protected String doInBackground(String... params) {
        try {
            return GBIFLoader.getSwedishTaxonKey(params[0]);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(final String result) {
        if (result != null) {
            view.setWebChromeClient(new WebChromeClient());
            view.addJavascriptInterface(new GBIFMapJSObject(bird, result), "GBIFMapData");
            view.loadUrl("file:///android_asset/maps/gbif-occurrences.html");
        }
    }

    @Override
    protected void onPreExecute() {
    }

    @Override
    protected void onProgressUpdate(Void... values) {
    }

    /**
     * The object that gets injected to the html page for Javascript access
     */
    public class GBIFMapJSObject {

        private String taxonKey;
        private int zoom = 4;
        private double latitude  = 63;
        private double longitude = 17.5;
        private Bird bird;

        public GBIFMapJSObject(Bird bird, String taxonKey) {
            this.bird = bird;
            this.taxonKey = taxonKey;
        }

        @JavascriptInterface
        public String getTaxonKey() {
            return taxonKey;
        }

        public void setTaxonKey(String taxonKey) {
            this.taxonKey = taxonKey;
        }

        public double getLatitude() {
            return latitude ;
        }

        public void setLatitude(double latitude ) {
            this.latitude = latitude ;
        }

        public double getLongitude() {
            return longitude;
        }

        public void setLongitude(double longitude) {
            this.longitude = longitude;
        }

        public int getZoom() {
            return zoom;
        }

        public void setZoom(int zoom) {
            this.zoom = zoom;
        }

        public String getSwedishSpecies() {
            return bird.getSwedishSpecies();
        }

    }
}
