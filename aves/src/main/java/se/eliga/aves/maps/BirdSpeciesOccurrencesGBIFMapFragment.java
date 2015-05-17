/*
 * Copyright (c) Claes Holmerson 2013, 2015. The program is licensed under GNU GPL v3. See LICENSE.txt for details.
 */

package se.eliga.aves.maps;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;

import se.eliga.aves.R;
import se.eliga.aves.birddetail.AbstractBirdSpeciesFragment;
import se.eliga.aves.model.Bird;

/**
 * Created by Claes on 2015-05-14.
 */
public class BirdSpeciesOccurrencesGBIFMapFragment  extends AbstractBirdSpeciesFragment {

    private MenuItem menuItemDefaultPosition;
    private MenuItem menuItemCurrentPosition;

    private WebView webView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        webView = (WebView) inflater.inflate(R.layout.webview_layout, container, false);

        webView.setWebChromeClient(new WebChromeClient());
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        setHasOptionsMenu(true);
        return webView;
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        loadBird(getCurrentBird());
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // inflater.inflate(R.menu.gbif_occurence_map_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        /* To be continued
        menuItemDefaultPosition = menu.findItem(R.id.gbif_occurrence_default);
        menuItemCurrentPosition = menu.findItem(R.id.gbif_occurrence_current);
        menuItemDefaultPosition.setChecked(true);
        */
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.isCheckable()) {
            menuItemCurrentPosition.setChecked(false);
            menuItemDefaultPosition.setChecked(false);
            item.setChecked(true);
        }
        return true;
    }

    @Override
    public void loadBird(Bird bird) {
        new LoadOccurrenceMapOperation(webView, bird).execute(bird.getLatinSpecies());
    }
}
