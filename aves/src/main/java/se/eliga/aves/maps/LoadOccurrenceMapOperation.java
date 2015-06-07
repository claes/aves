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
        private boolean showNationalParks = true;
        private boolean showNatureReserves = true;
        private boolean showAnimalReserves = true;


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

        @JavascriptInterface
        public double getLatitude() {
            return latitude ;
        }

        public void setLatitude(double latitude ) {
            this.latitude = latitude ;
        }

        @JavascriptInterface
        public double getLongitude() {
            return longitude;
        }

        public void setLongitude(double longitude) {
            this.longitude = longitude;
        }

        @JavascriptInterface
        public int getZoom() {
            return zoom;
        }

        public void setZoom(int zoom) {
            this.zoom = zoom;
        }

        @JavascriptInterface
        public String getSwedishSpecies() {
            return bird.getSwedishSpecies();
        }

        @JavascriptInterface
        public boolean isShowAnimalReserves() {
            return showAnimalReserves;
        }

        public void setShowAnimalReserves(boolean showAnimalReserves) {
            this.showAnimalReserves = showAnimalReserves;
        }

        @JavascriptInterface
        public boolean isShowNationalParks() {
            return showNationalParks;
        }

        public void setShowNationalParks(boolean showNationalParks) {
            this.showNationalParks = showNationalParks;
        }

        @JavascriptInterface
        public boolean isShowNatureReserves() {
            return showNatureReserves;
        }

        public void setShowNatureReserves(boolean showNatureReserves) {
            this.showNatureReserves = showNatureReserves;
        }
    }
}
