package net.holmerson.aves;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

public abstract class AbstractBirdSpeciesWikipediaFragment extends Fragment {

	public static final String LATIN_SPECIES = "LATIN_SPECIES";
	public static final String ENGLISH_SPECIES = "ENGLISH_SPECIES";

	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, 
        Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.wikipedia_layout, container, false);

        WebView webView = (WebView) view.findViewById(R.id.webview);
        webView.loadUrl(getUrl(getArguments().getString(LATIN_SPECIES), getArguments().getString(ENGLISH_SPECIES)));
        
        return view;
    }
	
	protected abstract String getUrl(String latinSpecies, String englishSpecies);

}