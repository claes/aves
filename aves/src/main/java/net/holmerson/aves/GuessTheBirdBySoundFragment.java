package net.holmerson.aves;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

public class GuessTheBirdBySoundFragment extends Fragment {


	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, 
        Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.guess_the_bird_by_sound_layout, container, false);
		return view;

    }
	
	
}
