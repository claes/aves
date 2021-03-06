/*
 * Copyright (c) Claes Holmerson 2013, 2015. The program is licensed under GNU GPL v3. See LICENSE.txt for details.
 */

package se.eliga.aves.birddetail;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import se.eliga.aves.Constants;
import se.eliga.aves.R;
import se.eliga.aves.model.Bird;

/**
 * Created by Claes on 2015-05-12.
 */
public class BirdSpeciesWebFragment extends AbstractBirdSpeciesFragment {

    public static final String LATIN_SPECIES = "LATIN_SPECIES";
    public static final String ENGLISH_SPECIES = "ENGLISH_SPECIES";

    private MenuItem menuItemWikipediaSwedish;
    private MenuItem menuItemWikipediaEnglish;
    private WebView webView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.detail_web_layout, container, false);
        setHasOptionsMenu(true);
        webView = (WebView) view.findViewById(R.id.webview);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (Uri.parse(url).getHost().contains("wikipedia.org")) {
                    return false;
                }
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(intent);
                return true;
            }
        });
        loadBird(getCurrentBird());
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.detail_web_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        menuItemWikipediaSwedish = menu.findItem(R.id.wikipedia_swedish);
        menuItemWikipediaEnglish = menu.findItem(R.id.wikipedia_english);
        SharedPreferences settings = getActivity().getSharedPreferences(Constants.BIRD_APP_SETTINGS, 0);
        WebType webType = WebType.lookupByCode(settings.getString(Constants.BIRD_WEB_TYPE, WebType.WIKIPEDIA_SV.getCode()));
        switch (webType) {
            case WIKIPEDIA_EN:
                menuItemWikipediaEnglish.setChecked(true);
                menuItemWikipediaSwedish.setChecked(false);
                break;
            case WIKIPEDIA_SV:
                menuItemWikipediaEnglish.setChecked(false);
                menuItemWikipediaSwedish.setChecked(true);
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Bird currentBird = getCurrentBird();
        switch (item.getItemId()) {
            case R.id.wikipedia_swedish:
                menuItemWikipediaEnglish.setChecked(false);
                menuItemWikipediaSwedish.setChecked(true);
                saveWebType(WebType.WIKIPEDIA_SV);
                loadBird(currentBird);
                break;
            case R.id.wikipedia_english:
                menuItemWikipediaEnglish.setChecked(true);
                menuItemWikipediaSwedish.setChecked(false);
                saveWebType(WebType.WIKIPEDIA_EN);
                loadBird(currentBird);
                break;
            default:
                return super.onOptionsItemSelected(item);
        }

        return true;
    }

    private void saveWebType(WebType webType) {
        SharedPreferences settings = getActivity().getSharedPreferences(Constants.BIRD_APP_SETTINGS, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(Constants.BIRD_WEB_TYPE, webType.getCode());
        editor.commit();
    }

    public void loadBirdInternal(Bird bird) {
        String url;
        SharedPreferences settings = getActivity().getSharedPreferences(Constants.BIRD_APP_SETTINGS, 0);
        WebType webType = WebType.lookupByCode(settings.getString(Constants.BIRD_WEB_TYPE, WebType.WIKIPEDIA_SV.getCode()));

        switch (webType) {
            case WIKIPEDIA_EN:
                url = getEnglishUrl(bird.getLatinSpecies(), bird.getEnglishSpecies());
            break;
            case WIKIPEDIA_SV:
            default:
            url = getSwedishUrl(bird.getLatinSpecies(), bird.getEnglishSpecies());
        }
        webView.loadUrl(url);
    }


    protected String getSwedishUrl(String latinSpecies, String englishSpecies) {
        String modifiedSpecies = latinSpecies.replaceAll(" ", "_");
        return "http://sv.m.wikipedia.org/wiki/"+modifiedSpecies + "#section_0";
    }

    protected String getEnglishUrl(String latinSpecies, String englishSpecies) {
        String modifiedSpecies = englishSpecies.replaceAll(" ", "_");
        return "http://en.m.wikipedia.org/wiki/"+modifiedSpecies + "#section_0";
    }

}