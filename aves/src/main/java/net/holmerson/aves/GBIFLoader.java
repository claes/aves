package net.holmerson.aves;

import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Class to load data from gbif.org API's
 * Created by Claes on 2013-07-19.
 */
public class GBIFLoader {

    public static final String ARTDATA_DATASET_KEY = "0938172b-2086-439c-a1dd-c21cb0109ed5";
    public static final String NUBKEY = "nubKey";

    private static String TAG = GBIFLoader.class.getName();

    public static String getSwedishTaxonKey(String latinSpecies) {
        InputStream inputStream = null;
        try {
        inputStream = getSwedishTaxonDataForSpecies(latinSpecies);
        if (inputStream != null) {
                String json = is2s(inputStream);
                JSONArray results = new JSONObject(json).getJSONArray("results");
                for (int i = 0; i < results.length(); i++) {
                    JSONObject jsonObject = results.getJSONObject(i);
                    String nubKey = jsonObject.getString(NUBKEY);
                    if (nubKey != null) {
                        return nubKey;
                    }
                }
        }
        } catch (Exception e) {
            Log.e(TAG, "Exception while retrieving metadata for GBIF of " + latinSpecies, e);
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (Exception e){
                    Log.e(TAG, "Exception while retrieving metadata for GBIF of " + latinSpecies, e);
                }
            }
        }
        return null;
    }

    private static InputStream getSwedishTaxonDataForSpecies(String species) {
        InputStream is = null;
        try {
            Uri.Builder builder = Uri.parse("http://api.gbif.org/v1/species").buildUpon();
            builder.appendQueryParameter("name", species);
            builder.appendQueryParameter("datasetKey", ARTDATA_DATASET_KEY);

            URL url = new URL(builder.toString());
            HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
            urlConn.setDefaultUseCaches(true);
            urlConn.setDoInput(true);
            urlConn.setDoOutput(true);
            urlConn.setUseCaches(true);
            is = new BufferedInputStream(url.openStream());

        } catch (Exception e) {
            Log.e(TAG, "Exception while retrieving input stream for metadata of " + species, e);
        }
        return is;
    }


    public static String is2s(InputStream is) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null)
            sb.append(line).append("\n");
        return sb.toString();
    }

}
