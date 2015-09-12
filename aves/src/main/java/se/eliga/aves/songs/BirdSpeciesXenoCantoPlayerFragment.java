/*
 * Copyright (c) Claes Holmerson 2013, 2015. The program is licensed under GNU GPL v3. See LICENSE.txt for details.
 */

package se.eliga.aves.songs;

import java.io.IOException;

import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
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
import android.widget.Spinner;

import se.eliga.aves.BirdApp;
import se.eliga.aves.Constants;
import se.eliga.aves.R;
import se.eliga.aves.birddetail.BirdSpeciesFragment;
import se.eliga.aves.model.Bird;

public class BirdSpeciesXenoCantoPlayerFragment extends ListFragment implements BirdSpeciesFragment,
        MediaPlayerControl {

    private static String TAG = BirdApp.class.getName();

    public static final String LATIN_SPECIES = "LATIN_SPECIES";

    private View view;
    private MediaController mediaController;
    private MediaPlayer mediaPlayer;
    private Handler handler = new Handler();
    private MenuItem audioRepeat;
    private MenuItem audioAutoNext;
    private ProgressBar progressBar;

    private int currentPosition = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.audio_player_layout, container,
                false);

        setHasOptionsMenu(true);

        mediaPlayer = new MediaPlayer();
        mediaController = new MediaController(getActivity(), false);
        mediaController.setMediaPlayer(this);
        mediaController.setAnchorView(view);

        mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {

            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                if (what == -38) {
                    return true; //not pretty.. http://stackoverflow.com/questions/15205855/error-19-0-mediaplayer/15206308#15206308
                } else {
                    return false;
                }
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
                                if (mediaController != null) {
                                    mediaController.show(0);
                                }
                                if (mediaPlayer != null) {
                                    try {
                                        mediaPlayer.start();
                                    } catch (Exception e) {
                                        Log.d(TAG, "Could not start mediaplayer", e);
                                    }
                                }
                            }
                        });
                    }
                });
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                if (audioAutoNext.isChecked()) {
                    int count = getListAdapter().getCount();
                    XenoCantoAudio nextAudio;
                    if (currentPosition < (count - 1)) {
                        nextAudio = (XenoCantoAudio) getListAdapter().getItem(++currentPosition);
                    } else {
                        nextAudio = (XenoCantoAudio) getListAdapter().getItem(0);
                    }
                    initiatePlay(nextAudio);
                }
            }
        });
        loadBird(getCurrentBird());
        return view;
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.audio_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

        audioRepeat = menu.findItem(R.id.audio_repeat);
        audioAutoNext = menu.findItem(R.id.audio_autonext);
        audioAutoNext.setChecked(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.isCheckable()) {
            audioAutoNext.setChecked(false);
            audioRepeat.setChecked(false);
            item.setChecked(true);
        }
        return true;
    }

    @Override
    public void onStop() {
        if (mediaController != null) {
            mediaController.hide();
        }
        mediaController = null;
        super.onStop();
        try {
            if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
            }
        } catch (Exception e) {
            Log.d(TAG, "Could not stop mediaplayer", e);
        }

        try {
            if (mediaPlayer != null) {
                mediaPlayer.release();
            }
        } catch (Exception e) {
            Log.d(TAG, "Could not release mediaplayer", e);
        }
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
        int currentPos = (mediaPlayer.getCurrentPosition() * 100);
        int duration = mediaPlayer.getDuration();
        if (duration != 0) {
            int percentage = currentPos / duration;
            return percentage;
        } else {
            return 0;
        }
    }

    @Override
    public int getCurrentPosition() {
        if (mediaPlayer != null) {
            return mediaPlayer.getCurrentPosition();
        } else {
            return 0;
        }
    }

    @Override
    public int getDuration() {
        return mediaPlayer.getDuration();
    }

    @Override
    public boolean isPlaying() {
        try {
            return mediaPlayer.isPlaying();
        } catch (Exception e) {
            Log.d(TAG, "Could not query mediaplayer", e);
            return false;
        }
    }

    @Override
    public void pause() {
        try {
            if (mediaPlayer.isPlaying())
                mediaPlayer.pause();
        } catch (Exception e) {
            Log.d(TAG, "Could not pause mediaplayer", e);
        }
    }

    @Override
    public void seekTo(int pos) {
        mediaPlayer.seekTo(pos);
    }

    @Override
    public void start() {
        try {
            mediaPlayer.start();
        } catch (Exception e) {
            Log.d(TAG, "Could not start mediaplayer", e);
        }
    }

    @Override
    public void onListItemClick(ListView l, View v, final int position, long id) {

        XenoCantoAudio audio = (XenoCantoAudio) getListAdapter().getItem(
                position);
        progressBar = (ProgressBar) getActivity()
                .findViewById(R.id.downloadProgress);
        currentPosition = position;
        try {
            if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
            }
        } catch (Exception e) {
            Log.d(TAG, "Could not stop mediaplayer", e);
        }
        initiatePlay(audio);
    }

    private void initiatePlay(XenoCantoAudio audio) {
        try {
            progressBar.setProgress(0);
            mediaPlayer.reset();
            mediaPlayer.setLooping(audioRepeat.isChecked());
            mediaPlayer.setDataSource(audio.getAudioURL());
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            progressBar.setVisibility(ProgressBar.VISIBLE);
            updateForCurrentAudio(audio);
            mediaPlayer.prepareAsync();
        } catch (Exception ex) {
            Log.e(TAG, "Could not open " + audio.getAudioURL()
                    + " for playback.", ex);
        }
    }

    private void updateForCurrentAudio(XenoCantoAudio currentAudio) {

        XenoCantoAudioListAdapter audioListAdapter = (XenoCantoAudioListAdapter) getListAdapter();
        audioListAdapter.setIsPlaying(currentAudio);
        ListView list = getListView();
        int start = list.getFirstVisiblePosition();
        for (int i = start, j = list.getLastVisiblePosition(); i <= j; i++) {
            View view = list.getChildAt(i - start);
            if (currentAudio == list.getItemAtPosition(i)) {
                audioListAdapter.getView(i, view, list).setSelected(true);
            } else {
                view.setSelected(false);
            }
        }
    }


    public Bird getCurrentBird() {
        Spinner spinner  = (Spinner) getActivity().getActionBar().getCustomView().findViewById(R.id.birdspecies_spinner);
        return (Bird) spinner.getSelectedItem();
    }

    @Override
    public void loadBird(Bird bird) {
        new LoadAudiosOperation(view, this).execute(bird.getLatinSpecies());
    }
}