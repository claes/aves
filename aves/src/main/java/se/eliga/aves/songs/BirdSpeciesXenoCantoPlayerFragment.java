package se.eliga.aves.songs;

import java.io.IOException;

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

import se.eliga.aves.BirdApp;
import se.eliga.aves.R;

public class BirdSpeciesXenoCantoPlayerFragment extends ListFragment implements
        MediaPlayerControl {

    private static String TAG = BirdApp.class.getName();

    public static final String LATIN_SPECIES = "LATIN_SPECIES";

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

        String species = getArguments().getString(LATIN_SPECIES);

        View view = inflater.inflate(R.layout.audio_player_layout, container,
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
                                mediaController.show(0);
                                mediaPlayer.start();
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

        new LoadAudiosOperation(view, this).execute(species);
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

        XenoCantoAudio audio = (XenoCantoAudio) getListAdapter().getItem(
                position);
        progressBar = (ProgressBar) getActivity()
                .findViewById(R.id.downloadProgress);


        currentPosition = position;
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
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
        } catch (IOException ex) {
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


}