/*
 * Copyright (c) Claes Holmerson 2013, 2015. The program is licensed under GNU GPL v3. See LICENSE.txt for details.
 */

package se.eliga.aves.songs;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;

import se.eliga.aves.R;

public class XenoCantoAudioListAdapter extends BaseAdapter implements ListAdapter {

	private List<XenoCantoAudio> audioList;
	
	private Context context;
	
	public XenoCantoAudioListAdapter(Context context, List<XenoCantoAudio> audioList) {
		this.context = context;
		this.audioList = audioList;
	}
	
	@Override
	public int getCount() {
		return audioList.size();
	}

	@Override
	public XenoCantoAudio getItem(int position) {
		return audioList.get(position);
	}

	@Override
	public long getItemId(int position) {
			return position;
	}

	public void setIsPlaying(XenoCantoAudio currentAudio) {
		for (XenoCantoAudio audio : audioList) {
			if (currentAudio == audio) {
				audio.setIsPlaying(true);
			} else {
				audio.setIsPlaying(false);
			}
		}
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		XenoCantoAudio audio = getItem(position);
		AudioHolder holder = null;
		if (convertView == null) {
			holder = new AudioHolder();
			convertView = LayoutInflater.from(context).inflate(
					R.layout.audio_row_layout, parent, false);
			holder.location = (TextView) convertView.findViewById(R.id.locationTextView);
			holder.recordist = (TextView) convertView.findViewById(R.id.recordistTextView);
			holder.songtype = (TextView) convertView.findViewById(R.id.songtypeTextView);
			holder.progressBar = (ProgressBar) convertView.findViewById(R.id.downloadProgress);
			convertView.setTag(holder);
		} else {
			holder = (AudioHolder) convertView.getTag();
		}
		holder.songtype.setText(audio.getSongtype().substring(0, 1).toUpperCase() + 
				audio.getSongtype().substring(1));
		holder.location.setText(audio.getLocation());
		holder.recordist.setText("\u00A9 " + audio.getRecordist() +", " + audio.getCountry() +  ", " + audio.getLicense().getCode());

		convertView.setSelected(audio.isPlaying());
		return convertView;
	}

	class AudioHolder {
		TextView location;
		TextView recordist;
		TextView songtype;
		ProgressBar progressBar;
	}

}
