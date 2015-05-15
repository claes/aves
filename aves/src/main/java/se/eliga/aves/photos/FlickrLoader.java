/*
 * Copyright (c) Claes Holmerson 2013, 2015. The program is licensed under GNU GPL v3. See LICENSE.txt for details.
 */

package se.eliga.aves.photos;

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

import se.eliga.aves.Keys;
import se.eliga.aves.model.Bird;

/**
 * Created by Claes on 2013-07-19.
 */
public class FlickrLoader {

    private static String TAG = FlickrLoader.class.getName();

    

    public static Bird initMetaData(Bird bird) {
        try {
            bird.setPhotos(getPhotosForBirdSpecies(bird.getLatinSpecies(), 10));
        } catch (Exception e) {
            Log.e(TAG, "Exception while fetching photos for " + bird, e);
        }
        return bird;
    }

    public static List<FlickrPhoto> getPhotosForBirdSpecies(String latinSpecies, int max) throws IOException,
            JSONException {

        InputStream inputStream = getInputStreamForLatinSpecies(latinSpecies);
        if (inputStream != null) {
            try {
                String json = is2s(inputStream);
                json = json.substring("jsonFlickrApi(".length(), json.length() - 1);
                JSONObject root = new JSONObject(json);
                if (!"ok".equals(root.getString("stat")))
                    throw new IOException("stat!=ok");
                JSONArray photosJson = root.getJSONObject("photos").getJSONArray("photo");
                int count = photosJson.length();
                ArrayList<FlickrPhoto> photos = new ArrayList<FlickrPhoto>(count);
                for (int i = 0; i < Math.min(max, photosJson.length()); i++) {
                	FlickrPhoto photo = createPhoto(photosJson.getJSONObject(i));
                    System.out.println("Photo: " + photo);
                	if (photo.getLicense().isUsable()) {
                		photos.add(photo);
                	}

                }
                return photos;
            } catch (Exception e) {
                Log.e(TAG, "Exception while retrieving input stream for metadata or photos of " + latinSpecies, e);
            } finally {
                inputStream.close();
            }
        }
        return null;
    }

    private static InputStream getInputStreamForLatinSpecies(String species) {
        InputStream is = null;
        try {
            Uri.Builder builder = Uri.parse("https://api.flickr.com/services/rest/").buildUpon();
            builder.appendQueryParameter("method", "flickr.groups.pools.getPhotos");
            builder.appendQueryParameter("api_key", Keys.FLICKR_API_KEY);
            builder.appendQueryParameter("tags", species);
            builder.appendQueryParameter("group_id", "42637302@N00");
            builder.appendQueryParameter("format", "json");
            builder.appendQueryParameter("extras", "license");

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

    private static InputStream getInputStreamForImageUrl(String urlString) {
        InputStream is = null;
        try {
            URL url = new URL(urlString);
            HttpURLConnection urlConn = (HttpURLConnection) new URL(urlString).openConnection();
            urlConn.setDefaultUseCaches(true);
            urlConn.setDoInput(true);
            urlConn.setDoOutput(true);
            urlConn.setUseCaches(true);
            is = new BufferedInputStream(url.openStream());
        } catch (Exception e) {
            Log.e(TAG, "Exception while retrieving input stream for " + urlString, e);
        }
        return is;
    }


    private static FlickrPhoto createPhoto(JSONObject photoJson) throws JSONException {
        String id = photoJson.getString("id");
        int farm = photoJson.getInt("farm");
        String secret = photoJson.getString("secret");
        String server = photoJson.getString("server");
        String title = photoJson.getString("title");
        String ownerName = photoJson.getString("ownername");
        FlickrPhoto.License license = FlickrPhoto.License.fromString(photoJson.getString("license"));
        return new FlickrPhoto(id, farm, secret, server, title, ownerName, license);
    }

    public static FlickrPhoto insertPhoto(FlickrPhoto photo) {
        photo.setImage(BitmapFactory.decodeStream(getInputStreamForImageUrl(photo.getSmallSquareUrl())));
        return photo;
    }

    public static List<String> retrieveUrls(List<FlickrPhoto> photos, String size) {
        List<String> urls = new ArrayList<String>();
        for (FlickrPhoto photo : photos) {
            urls.add(photo.createUrl(size));
        }
        return urls;
    }

    public static String is2s(InputStream is) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null)
            sb.append(line).append("\n");
        return sb.toString();
    }
    
//    <licenses>
//    <license id="0" name="All Rights Reserved" url="" />
//    <license id="1" name="Attribution-NonCommercial-ShareAlike License" url="http://creativecommons.org/licenses/by-nc-sa/2.0/" />
//    <license id="2" name="Attribution-NonCommercial License" url="http://creativecommons.org/licenses/by-nc/2.0/" />
//    <license id="3" name="Attribution-NonCommercial-NoDerivs License" url="http://creativecommons.org/licenses/by-nc-nd/2.0/" />
//    <license id="4" name="Attribution License" url="http://creativecommons.org/licenses/by/2.0/" />
//    <license id="5" name="Attribution-ShareAlike License" url="http://creativecommons.org/licenses/by-sa/2.0/" />
//    <license id="6" name="Attribution-NoDerivs License" url="http://creativecommons.org/licenses/by-nd/2.0/" />
//    <license id="7" name="No known copyright restrictions" url="http://flickr.com/commons/usage/" />
//    <license id="8" name="United States Government Work" url="http://www.usa.gov/copyright.shtml" />
//    </licenses>    
}
