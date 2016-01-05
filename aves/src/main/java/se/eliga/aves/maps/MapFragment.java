/*
 * Copyright (c) Claes Holmerson 2013, 2015. The program is licensed under GNU GPL v3. See LICENSE.txt for details.
 */

package se.eliga.aves.maps;

import android.content.SharedPreferences;
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

import se.eliga.aves.BuildConfig;
import se.eliga.aves.Constants;
import se.eliga.aves.R;
import se.eliga.aves.birddetail.AbstractBirdSpeciesFragment;
import se.eliga.aves.model.Bird;

/**
 * Created by Claes on 2015-05-14.
 */
public class MapFragment extends AbstractBirdSpeciesFragment {

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
        inflater.inflate(R.menu.map_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        menuItemDefaultPosition = menu.findItem(R.id.map_region_sweden);
        menuItemCurrentPosition = menu.findItem(R.id.map_region_current_position);
        menuItemWesternPalearctisPosition = menu.findItem(R.id.map_region_western_palearctis);
        menuItemShowOccurrences = menu.findItem(R.id.map_show_gbif_occurrences);
        menuItemDistribution = menu.findItem(R.id.map_show_distribution);
        if (Constants.BUILD_TYPE_RELEASE.equals(BuildConfig.BUILD_TYPE)) {
            menuItemShowOccurrences.setVisible(false);
            menuItemDistribution.setVisible(false);
        } else {
            menuItemShowOccurrences.setVisible(true);
            menuItemDistribution.setVisible(true);
        }


            SharedPreferences settings = getActivity().getSharedPreferences(Constants.BIRD_APP_SETTINGS, 0);
        MapType mapType = MapType.lookupByCode(settings.getString(Constants.BIRD_MAP_TYPE, MapType.OCCURRENCE.getCode()));

        menuItemShowOccurrences.setChecked(MapType.OCCURRENCE.equals(mapType));
        menuItemDistribution.setChecked(MapType.DISTRIBUTION.equals(mapType));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.map_region_sweden:
                saveRegionChoice(MapRegion.SWEDEN);
                goToSweden();
                return true;
            case R.id.map_region_current_position:
                saveRegionChoice(MapRegion.CURRENT);
                goToCurrentPosition();
                return true;
            case R.id.map_region_western_palearctis:
                saveRegionChoice(MapRegion.WESTERN_PALEARCTIS);
                goToWesternPalearctis();
                return true;
            case R.id.map_region_world:
                saveRegionChoice(MapRegion.WORLD);
                goToWorld();
                return true;
            case R.id.map_show_gbif_occurrences:
                saveMapTypeChoice(MapType.OCCURRENCE);
                menuItemShowOccurrences.setChecked(true);
                menuItemDistribution.setChecked(false);
                loadBird(getCurrentBird());
                return true;
            case R.id.map_show_distribution:
                saveMapTypeChoice(MapType.DISTRIBUTION);
                menuItemShowOccurrences.setChecked(false);
                menuItemDistribution.setChecked(true);
                loadBird(getCurrentBird());
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void loadBirdInternal(Bird bird) {
        SharedPreferences settings = getActivity().getSharedPreferences(Constants.BIRD_APP_SETTINGS, 0);
        MapRegion mapRegion = MapRegion.lookupByCode(settings.getString(Constants.BIRD_MAP_REGION, MapRegion.SWEDEN.getCode()));
        MapType mapType = MapType.lookupByCode(settings.getString(Constants.BIRD_MAP_TYPE, MapType.OCCURRENCE.getCode()));
        if (Constants.BUILD_TYPE_RELEASE.equals(BuildConfig.BUILD_TYPE)) {
            mapType = MapType.OCCURRENCE;
        }
        double lat;
        double lng;
        int zoom;
        switch (mapRegion) {
            case WESTERN_PALEARCTIS:
                lat = 43; lng = 13; zoom = 2;
                break;
            case WORLD:
                lat = 0; lng = 0; zoom = 1;
                break;
            case SWEDEN:
            default:
                lat = 64; lng = 17.5; zoom = 4;
        }
        new LoadMapOperation(webView, bird, mapType, lat, lng, zoom).execute(bird.getLatinSpecies());
    }

    private void saveRegionChoice(MapRegion region) {
        SharedPreferences settings = getActivity().getSharedPreferences(Constants.BIRD_APP_SETTINGS, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(Constants.BIRD_MAP_REGION, region.getCode());
        editor.commit();
    }

    private void saveMapTypeChoice(MapType type) {
        SharedPreferences settings = getActivity().getSharedPreferences(Constants.BIRD_APP_SETTINGS, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(Constants.BIRD_MAP_TYPE, type.getCode());
        editor.commit();
    }

    public void goToSweden() {
        new GoToDefinedPositionOperation(webView, 63, 17.5, 4).execute();
    }

    public void goToCurrentPosition() {
        new GoToCurrentPositionOperation(webView).execute();
    }

    public void goToWesternPalearctis() {
        new GoToDefinedPositionOperation(webView, 42, 13, 2).execute();
    }

    public void goToWorld() {
        new GoToDefinedPositionOperation(webView, 0, 0, 1).execute();
    }
}
