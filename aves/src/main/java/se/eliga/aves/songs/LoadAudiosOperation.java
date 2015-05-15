package se.eliga.aves.songs;

import java.util.List;

import android.os.AsyncTask;
import android.support.v4.app.ListFragment;
import android.view.View;

public class LoadAudiosOperation extends AsyncTask<String, Void, List<XenoCantoAudio>> {

    private View view;
    private XenoCantoAudioListAdapter listAdapter;
    private ListFragment listFragment;


    public LoadAudiosOperation(View context, ListFragment listFragment) {
        this.view = context;
        this.listFragment = listFragment;
    }

    @Override
    protected List<XenoCantoAudio> doInBackground(String... params) {
        try {
            return XenoCantoLoader.getAudiosForBirdSpecies(params[0]);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(final List<XenoCantoAudio> result) {
        if (result != null) {
        	listAdapter = new XenoCantoAudioListAdapter(view.getContext(), result);
        	listFragment.setListAdapter(listAdapter);
        }
    }

    @Override
    protected void onPreExecute() {
    }

    @Override
    protected void onProgressUpdate(Void... values) {
    }
}
