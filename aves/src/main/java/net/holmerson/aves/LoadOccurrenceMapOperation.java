package net.holmerson.aves;

import java.util.ArrayList;
import java.util.List;

import ru.truba.touchgallery.GalleryWidget.BasePagerAdapter.OnItemChangeListener;
import ru.truba.touchgallery.GalleryWidget.GalleryViewPager;
import ru.truba.touchgallery.GalleryWidget.UrlPagerAdapter;
import android.os.AsyncTask;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.TextView;

public class LoadOccurrenceMapOperation extends
        AsyncTask<String, Void, String> {

    private WebView view;

    public LoadOccurrenceMapOperation(WebView context) {
        this.view = context;
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
            view.addJavascriptInterface(new GBIFMapJSObject(result), "GBIFMapData");
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

        public GBIFMapJSObject(String taxonKey) {
            this.taxonKey = taxonKey;
        }

        @JavascriptInterface
        public String getTaxonKey() {
            return taxonKey;
        }

        public void setTaxonKey(String taxonKey) {
            this.taxonKey = taxonKey;
        }
    }
}
