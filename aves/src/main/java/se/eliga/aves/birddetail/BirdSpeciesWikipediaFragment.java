/*
 * Copyright (c) Claes Holmerson 2013, 2015. The program is licensed under GNU GPL v3. See LICENSE.txt for details.
 */

package se.eliga.aves.birddetail;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import se.eliga.aves.R;

/**
 * Created by Claes on 2015-05-12.
 */
public class BirdSpeciesWikipediaFragment extends Fragment {

    public static final String LATIN_SPECIES = "LATIN_SPECIES";
    public static final String ENGLISH_SPECIES = "ENGLISH_SPECIES";

    private MenuItem menuItemSwedish;
    private MenuItem menuItemEnglish;
    private WebView webView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.wikipedia_layout, container, false);
        setHasOptionsMenu(true);

        webView = (WebView) view.findViewById(R.id.webview);
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.wikipedia_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        menuItemSwedish = menu.findItem(R.id.wikipedia_swedish);
        menuItemEnglish = menu.findItem(R.id.wikipedia_english);
        menuItemSwedish.setChecked(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.isCheckable()) {
            menuItemEnglish.setChecked(false);
            menuItemSwedish.setChecked(false);
            item.setChecked(true);
            loadPage();
        }
        return true;
    }

    @Override
    public void onStart() {
        super.onStart();
        loadPage();
    }

    private void loadPage() {
        String url;
        if (menuItemEnglish != null && menuItemEnglish.isChecked()) {
            url = getEnglishUrl(getArguments().getString(LATIN_SPECIES), getArguments().getString(ENGLISH_SPECIES));
        } else {
            url = getSwedishUrl(getArguments().getString(LATIN_SPECIES), getArguments().getString(ENGLISH_SPECIES));
        }
        webView.loadUrl(url);
    }

    protected String getSwedishUrl(String latinSpecies, String englishSpecies) {
        String modifiedSpecies = latinSpecies.replaceAll(" ", "_");
        return "http://sv.m.wikipedia.org/wiki/"+modifiedSpecies;
    }

    protected String getEnglishUrl(String latinSpecies, String englishSpecies) {
        String modifiedSpecies = englishSpecies.replaceAll(" ", "_");
        return "http://en.m.wikipedia.org/wiki/"+modifiedSpecies;
    }

}
