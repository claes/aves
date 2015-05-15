/*
 * Copyright (c) Claes Holmerson 2013, 2015. The program is licensed under GNU GPL v3. See LICENSE.txt for details.
 */

package se.eliga.aves.songs;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.net.Uri;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import se.eliga.aves.model.Bird;
import se.eliga.aves.model.License;
import se.eliga.aves.photos.FlickrPhoto;

public class XenoCantoLoader {

	private static String TAG = XenoCantoLoader.class.getName();

	public static Bird initMetaData(Bird bird) {
		try {
			bird.setAudios(getAudiosForBirdSpecies(bird.getLatinSpecies()));
		} catch (Exception e) {
			Log.e(TAG, "Exception while fetching photos for " + bird, e);
		}
		return bird;
	}

	public static List<XenoCantoAudio> getAudiosForBirdSpecies(
			String latinSpecies) throws IOException {

		InputStream inputStream = getInputStreamForLatinSpecies(latinSpecies);
		if (inputStream != null) {
			try {
				String json = is2s(inputStream);
				JSONObject root = new JSONObject(json);
				JSONArray audiosJson = root.getJSONArray("recordings");
				List<XenoCantoAudio> audioList = new ArrayList<XenoCantoAudio>();
				for (int i = 0; i < audiosJson.length(); i++) {

					JSONObject audioObject = audiosJson.getJSONObject(i);
					XenoCantoAudio audio = new XenoCantoAudio();
					audio.setXenoCantoIdNumber(audioObject.getString("id"));
					audio.setGenus(audioObject.getString("gen"));
					audio.setSpecies(audioObject.getString("sp"));
					audio.setEnglish(audioObject.getString("en"));
					audio.setSubspecies(audioObject.getString("ssp"));
					audio.setRecordist(audioObject.getString("rec"));
					audio.setCountry(audioObject.getString("cnt"));
					audio.setLocation(audioObject.getString("loc"));
					audio.setLatitude(audioObject.getString("lat"));
					audio.setLongitude(audioObject.getString("lng"));
					audio.setSongtype(audioObject.getString("type"));
					audio.setAudioURL(unescapeUrl(audioObject.getString("file")));
					audio.setLicense(parseLicense(unescapeUrl(audioObject.getString("lic"))));
					if (audio.getLicense().isUsable()) {
						audioList.add(audio);
					}
				}
				return audioList;
			} catch (Exception e) {
				Log.e(TAG,
						"Exception while retrieving input stream for metadata or photos of "
								+ latinSpecies, e);
			} finally {
				inputStream.close();
			}
		}
		return null;
	}

	private static String unescapeUrl(String url) {
		return url.replace("\\", ""); //TODO this is strictly not correct but good enough for now
	}

	private static License parseLicense(String url) {
		Pattern pattern = Pattern.compile("licenses/(by-nc|by-nc-nd|by-sa|by-nd|by-nc-sa)/");
		Matcher matcher = pattern.matcher(url);
		if (matcher.find()) {
			String code = matcher.group(1);
			return License.fromXenoCantoCode(code);
		} else {
			return License.ALL_RIGHTS_RESERVED;
		}
	}

	private static InputStream getInputStreamForLatinSpecies(String species) {
		InputStream is = null;
		try {
			Uri.Builder builder = Uri
					.parse("http://www.xeno-canto.org/api/2/recordings").buildUpon();
			builder.appendQueryParameter("query", species + " q>:C"); //better quality

			URL url = new URL(builder.toString());
			HttpURLConnection urlConn = (HttpURLConnection) url
					.openConnection();
			urlConn.setDefaultUseCaches(true);
			urlConn.setDoInput(true);
			urlConn.setDoOutput(true);
			urlConn.setUseCaches(true);
			is = new BufferedInputStream(url.openStream());

		} catch (Exception e) {
			Log.e(TAG,
					"Exception while retrieving input stream for metadata of "
							+ species, e);
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
