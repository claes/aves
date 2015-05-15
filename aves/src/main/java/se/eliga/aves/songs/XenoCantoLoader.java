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

import android.net.Uri;
import android.util.Log;

import se.eliga.aves.model.Bird;

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
				List<XenoCantoAudio> audioList = new ArrayList<XenoCantoAudio>();
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(inputStream));
				String line = reader.readLine();
				while (line != null) {
					String[] parts = line.split(";");
					XenoCantoAudio audio = new XenoCantoAudio();
					if (parts.length >= 11) {
						audio.setXenoCantoIdNumber(parts[0]);
						audio.setGenus(parts[1]);
						audio.setSpecies(parts[2]);
						audio.setEnglish(parts[3]);
						audio.setSubspecies(parts[4]);
						audio.setRecordist(parts[5]);
						audio.setCountry(parts[6]);
						audio.setLocation(parts[7]);
						audio.setLatitude(parts[8]);
						audio.setLongitude(parts[9]);
						audio.setSongtype(parts[10]);
						audio.setAudioURL(parts[12]);
						audioList.add(audio);
					}
					line = reader.readLine();
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

	private static InputStream getInputStreamForLatinSpecies(String species) {
		InputStream is = null;
		try {
			Uri.Builder builder = Uri
					.parse("http://www.xeno-canto.org/csv.php").buildUpon();
			builder.appendQueryParameter("species", species);
			builder.appendQueryParameter("representative=", "0");
			builder.appendQueryParameter("fileinfo", "1");

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

}
