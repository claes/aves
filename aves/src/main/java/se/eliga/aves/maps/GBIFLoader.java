/*
 * Copyright (c) Claes Holmerson 2013, 2015. The program is licensed under GNU GPL v3. See LICENSE.txt for details.
 */

package se.eliga.aves.maps;

import android.net.Uri;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Class to load data from gbif.org API's
 * Created by Claes on 2013-07-19.
 */
public class GBIFLoader {

    public static final String ARTDATA_DATASET_KEY = "0938172b-2086-439c-a1dd-c21cb0109ed5";
    public static final String IUCN_DATASET_KEY = "19491596-35ae-4a91-9a98-85cf505f1bd3";
    public static final String NUBKEY = "nubKey";
    public static final String TAXONID = "taxonID";

    private static String TAG = GBIFLoader.class.getName();

    public static String getSwedishTaxonKey(String latinSpecies) {
        InputStream inputStream = null;
        try {
        inputStream = getTaxonDataForSpecies(latinSpecies, ARTDATA_DATASET_KEY);
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


    public static String getBirdLifeSpcRecId(String latinSpecies) {
        InputStream inputStream = null;
        try {
            inputStream = getTaxonDataForSpecies(latinSpecies, IUCN_DATASET_KEY);
            if (inputStream != null) {
                String json = is2s(inputStream);
                JSONArray results = new JSONObject(json).getJSONArray("results");
                for (int i = 0; i < results.length(); i++) {
                    JSONObject jsonObject = results.getJSONObject(i);
                    String taxonId = jsonObject.getString(TAXONID);
                    if (taxonId != null) {
                        //Subtract 106000000 for some reason?
                        int taxonIdInt = Integer.parseInt(taxonId);
                        return Integer.toString(taxonIdInt - 100600000);
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

    private static InputStream getTaxonDataForSpecies(String species, String datasetKey) {
        InputStream is = null;
        try {
            Uri.Builder builder = Uri.parse("http://api.gbif.org/v1/species").buildUpon();
            builder.appendQueryParameter("name", species);
            builder.appendQueryParameter("datasetKey", datasetKey);

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
