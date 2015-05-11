package net.holmerson.aves;

import java.io.IOException;

import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.MediaController;
import android.widget.MediaController.MediaPlayerControl;
import android.widget.ProgressBar;

public class BirdSpeciesXenoCantoPlayerFragment extends ListFragment implements
        MediaPlayerControl {

    private static String TAG = BirdApplication.class.getName();

    public static final String LATIN_SPECIES = "LATIN_SPECIES";

    private MediaController mediaController;
    private MediaPlayer mediaPlayer;
    private Handler handler = new Handler();
    private MenuItem audioRepeat;
    private MenuItem audioAutoNext;

	/*
    public static final Fragment newInstance(String latinSpecies) {
		Fragment f = new BirdSpeciesXenoCantoPlayerFragment();
		Bundle bundle = new Bundle(1);
		bundle.putString(LATIN_SPECIES, latinSpecies);
		f.setArguments(bundle);
		return f;
	}
	*/

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        String species = getArguments().getString(LATIN_SPECIES);

        View view = inflater.inflate(R.layout.audio_player_layout, container,
                false);

        setHasOptionsMenu(true);

        mediaPlayer = new MediaPlayer();
        mediaController = new MediaController(getActivity(), false);
        mediaController.setMediaPlayer(this);
        mediaController.setAnchorView(view);

        new LoadAudiosOperation(view, this).execute(species);
        return view;
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // TODO Add your menu entries here
        inflater.inflate(R.menu.audio_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        audioRepeat = menu.findItem(R.id.audio_repeat);
        audioRepeat.setChecked(true);
        audioAutoNext = menu.findItem(R.id.audio_autonext);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.isCheckable()) {
            item.setChecked(!item.isChecked());
        }
        return true;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mediaPlayer.stop();
        mediaPlayer.release();
        mediaPlayer = null;
    }

    @Override
    public boolean canPause() {
        return true;
    }

    @Override
    public boolean canSeekBackward() {
        return false;
    }

    @Override
    public boolean canSeekForward() {
        return false;
    }

    @Override
    public int getBufferPercentage() {
        int percentage = (mediaPlayer.getCurrentPosition() * 100)
                / mediaPlayer.getDuration();

        return percentage;
    }

    @Override
    public int getCurrentPosition() {
        return mediaPlayer.getCurrentPosition();
    }

    @Override
    public int getDuration() {
        return mediaPlayer.getDuration();
    }

    @Override
    public boolean isPlaying() {
        return mediaPlayer.isPlaying();
    }

    @Override
    public void pause() {
        if (mediaPlayer.isPlaying())
            mediaPlayer.pause();
    }

    @Override
    public void seekTo(int pos) {
        mediaPlayer.seekTo(pos);
    }

    @Override
    public void start() {
        mediaPlayer.start();
    }

    @Override
    public void onListItemClick(ListView l, View v, final int position, long id) {

        if (mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
        }
        XenoCantoAudio audio = (XenoCantoAudio) getListAdapter().getItem(
                position);

        try {
            mediaPlayer.reset();
            mediaPlayer.setDataSource(audio.getAudioURL());
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            final ProgressBar progressBar = (ProgressBar) getActivity()
                    .findViewById(R.id.downloadProgress);
            progressBar.setVisibility(ProgressBar.VISIBLE);
            progressBar.setProgress(0);
            mediaPlayer.prepareAsync();

            mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {

                @Override
                public boolean onError(MediaPlayer mp, int what, int extra) {
                    // TODO Auto-generated method stub
                    return false;
                }
            });

            mediaPlayer
                    .setOnBufferingUpdateListener(new MediaPlayer.OnBufferingUpdateListener() {
                        @Override
                        public void onBufferingUpdate(MediaPlayer mp,
                                                      int percent) {
                            progressBar.setProgress(percent);
                        }
                    });
            mediaPlayer
                    .setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                        @Override
                        public void onPrepared(MediaPlayer mp) {
                            handler.post(new Runnable() {
                                public void run() {
                                    progressBar.setVisibility(ProgressBar.GONE);
                                    mediaController.show(0);
                                    mediaPlayer.start();
                                }
                            });
                        }
                    });
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    if (audioRepeat.isChecked()) {
                        mediaPlayer.seekTo(0);
                        progressBar.setVisibility(ProgressBar.VISIBLE);
                        progressBar.setProgress(0);
                        mediaPlayer.start();
                    } else if (audioAutoNext.isChecked()) {
                        int count = getListAdapter().getCount();
                        XenoCantoAudio nextAudio;
                        if (position < (count - 1)) {
                            nextAudio = (XenoCantoAudio) getListAdapter().getItem(position + 1);
                        } else {
                            nextAudio = (XenoCantoAudio) getListAdapter().getItem(0);
                        }
                        try {
                            mediaPlayer.reset();
                            mediaPlayer.setDataSource(nextAudio.getAudioURL());
                            progressBar.setVisibility(ProgressBar.VISIBLE);
                            progressBar.setProgress(0);
                            mediaPlayer.prepareAsync();
                        } catch (IOException ex) {
                            Log.e(TAG, "Could not open " + nextAudio.getAudioURL()
                                    + " for playback.", ex);
                        }
                    }
                }
            });
        } catch (IOException e) {
            Log.e(TAG, "Could not open " + audio.getAudioURL()
                    + " for playback.", e);
        }
    }

    //Doesn't work. Not sure how to update to highlight the "correct one" in a long list
    private void updateRow(int position) {
        ListView listView = getListView();
        int firstPos = listView.getFirstVisiblePosition();
        int lastPos = listView.getLastVisiblePosition();
        for (int i = firstPos; i <= lastPos; i++) {
            View view = listView.getChildAt(i);
            if (position == i) {
                view.setBackgroundColor(Color.RED);
            } else {
                view.setBackgroundColor(Color.GREEN);
            }
        }
    }
}