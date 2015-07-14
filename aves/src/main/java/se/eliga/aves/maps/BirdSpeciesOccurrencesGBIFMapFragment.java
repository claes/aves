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
    private MenuItem menuItemWesternPalearctisPosition;
    private MenuItem menuItemShowOccurrences;
    private MenuItem menuItemDistribution;


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
        inflater.inflate(R.menu.gbif_occurence_map_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        menuItemDefaultPosition = menu.findItem(R.id.gbif_region_sweden);
        menuItemCurrentPosition = menu.findItem(R.id.gbif_region_current_position);
        menuItemWesternPalearctisPosition = menu.findItem(R.id.gbif_region_western_palearctis);
        menuItemShowOccurrences = menu.findItem(R.id.gbif_show_occurrences);
        menuItemDistribution = menu.findItem(R.id.gbif_show_distribution);
        menuItemShowOccurrences.setChecked(true);
        menuItemDistribution.setChecked(false);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.gbif_region_sweden:
                loadBird(getCurrentBird());
                return true;
            case R.id.gbif_region_current_position:
                goToCurrentPosition();
                return true;
            case R.id.gbif_region_western_palearctis:
                goToWesternPalearctis();
                return true;
            case R.id.gbif_show_occurrences:
                menuItemShowOccurrences.setChecked(true);
                menuItemDistribution.setChecked(false);
                loadBird(getCurrentBird());
                return true;
            case R.id.gbif_show_distribution:
                menuItemShowOccurrences.setChecked(false);
                menuItemDistribution.setChecked(true);
                loadBird(getCurrentBird());
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void loadBird(Bird bird) {
        new LoadMapOperation(webView, bird, (menuItemShowOccurrences == null || menuItemShowOccurrences.isChecked()) ?
                LoadMapOperation.MapType.OCCURRENCE : LoadMapOperation.MapType.DISTRIBUTION).execute(bird.getLatinSpecies());
    }

    public void goToCurrentPosition() {
        new GoToCurrentPositionOperation(webView).execute();
    }

    public void goToWesternPalearctis() {
        new GoToDefinedPositionOperation(webView, 42, 13, 2).execute();
    }

}
