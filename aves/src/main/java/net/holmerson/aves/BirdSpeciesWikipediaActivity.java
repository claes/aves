package net.holmerson.aves;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

public class BirdSpeciesWikipediaActivity extends Activity {
	
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wikipedia_layout);
        
        final String species = getIntent().getExtras().getString(MainActivity.ENGLISH_SPECIES);
        String modifiedSpecies = species.replaceAll(" ", "_");

        WebView webView = (WebView) findViewById(R.id.webview);
        final Activity activity = this;
        webView.setWebChromeClient(new WebChromeClient() {
          public void onProgressChanged(WebView view, int progress) {
            // Activities and WebViews measure progress with different scales.
            // The progress meter will automatically disappear when we reach 100%
            activity.setProgress(progress * 1000);
          }
        });
        webView.setWebViewClient(new WebViewClient() {
          public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            Toast.makeText(activity, "Could not load " +  species + " from Wikipedia", Toast.LENGTH_SHORT).show();
          }
        });
        
        webView.loadUrl("http://en.m.wikipedia.org/wiki/"+modifiedSpecies);
    }	

}
