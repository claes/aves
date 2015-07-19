/*
 * Copyright (c) Claes Holmerson 2013, 2015. The program is licensed under GNU GPL v3. See LICENSE.txt for details.
 */

package se.eliga.aves;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;

import se.eliga.aves.model.Bird;

public class AboutFragment extends Fragment {


    public AboutFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_about, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        WebView webView = (WebView) getView()
                .findViewById(R.id.about_text);
        webView.loadUrl("file:///android_asset/about.html");
        webView.setBackgroundColor(0x00FFFFFF);
        webView.setVisibility(View.VISIBLE);
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        webView.addJavascriptInterface(new AboutJSObject(getVersion(), BuildConfig.BUILDTIME, BuildConfig.BUILDTYPE), "AboutData");
    }

    private String getVersion() {
        try {
            String versionName =
            getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0).versionName;
            return versionName;
        } catch (Exception e) {
            return null;
        }
    }


    /**
     * The object that gets injected to the html page for Javascript access
     */
    public class AboutJSObject {

        private String version;
        private String buildTime;
        private String buildType;

        public AboutJSObject(String version, String buildTime, String buildType) {
            this.version = version;
            this.buildTime = buildTime;
            this.buildType = buildType;
        }

        @JavascriptInterface
        public String getVersion() {
            return version;
        }

        @JavascriptInterface
        public String getBuildTime() {
            return buildTime;
        }

        @JavascriptInterface
        public String getBuildType() {
            return buildType;
        }
    }


}
