/*
 * Copyright (c) Claes Holmerson 2013, 2015. The program is licensed under GNU GPL v3. See LICENSE.txt for details.
 */

package se.eliga.aves.maps;

import android.location.Location;
import android.os.AsyncTask;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

import se.eliga.aves.BirdApp;
import se.eliga.aves.model.Bird;

public class LoadOccurrenceMapOperation extends
        AsyncTask<String, Void, String[]> {

    private WebView view;
    private Bird bird;

    public LoadOccurrenceMapOperation(WebView context, Bird bird) {
        this.view = context;
        this.bird = bird;
    }

    @Override
    protected String[] doInBackground(String... params) {
        try {
            String birdLifeSpcRecId = GBIFLoader.getBirdLifeSpcRecId(params[0]);
            return new String[] {birdLifeSpcRecId};
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(final String[] result) {
        if (result != null) {
            view.setWebChromeClient(new WebChromeClient());
            view.addJavascriptInterface(new GBIFMapJSObject(bird, bird.getDyntaxaTaxonId(), result[0], 63, 17.5, 4, true, false), "GBIFMapData");
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

        private String swedishTaxonKey;
        private String birdLifeSpcRecId;
        private int zoom;
        private double latitude;
        private double longitude;
        private Bird bird;
        private boolean showOccurrences = true;
        private boolean showDistribution = false;
        private boolean showNationalParks = true;
        private boolean showNatureReserves = true;
        private boolean showAnimalReserves = true;
        private boolean showProhibitedEntry = true;

        public GBIFMapJSObject(Bird bird, String taxonKey, String birdLifeSpcRecId,
                               double latitude, double longitude, int zoom,
                               boolean showOccurrences, boolean showDistribution) {
            this.bird = bird;
            this.swedishTaxonKey = taxonKey;
            this.birdLifeSpcRecId = birdLifeSpcRecId;
            this.latitude = latitude;
            this.longitude = longitude;
            this.zoom = zoom;
            this.showOccurrences = showOccurrences;
            this.showDistribution = showDistribution;
        }


        @JavascriptInterface
        public String getSwedishTaxonKey() {
            return swedishTaxonKey;
        }

        public void setSwedishTaxonKey(String swedishTaxonKey) {
            this.swedishTaxonKey = swedishTaxonKey;
        }

        @JavascriptInterface
        public String getBirdLifeSpcRecId() {
            return birdLifeSpcRecId;
        }

        public void setBirdLifeSpcRecId(String birdLifeSpcRecId) {
            this.birdLifeSpcRecId = birdLifeSpcRecId;
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

        @JavascriptInterface
        public boolean isShowProhibitedEntry() {
            return showProhibitedEntry;
        }

        public void setShowProhibitedEntry(boolean showProhibitedEntry) {
            this.showProhibitedEntry = showProhibitedEntry;
        }

        @JavascriptInterface
        public boolean isShowDistribution() {
            return showDistribution;
        }

        public void setShowDistribution(boolean showDistribution) {
            this.showDistribution = showDistribution;
        }

        @JavascriptInterface
        public boolean isShowOccurrences() {
            return showOccurrences;
        }

        public void setShowOccurrences(boolean showOccurrences) {
            this.showOccurrences = showOccurrences;
        }
    }
}
