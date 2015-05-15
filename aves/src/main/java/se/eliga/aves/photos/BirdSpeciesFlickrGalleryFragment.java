package se.eliga.aves.photos;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import se.eliga.aves.R;

public class BirdSpeciesFlickrGalleryFragment extends Fragment {

	public static final String LATIN_SPECIES = "LATIN_SPECIES";
	
	public static final BirdSpeciesFlickrGalleryFragment newInstance(String latinSpecies)
	{
		BirdSpeciesFlickrGalleryFragment f = new BirdSpeciesFlickrGalleryFragment();
		Bundle bundle = new Bundle(1);
	    bundle.putString(LATIN_SPECIES, latinSpecies);
	    f.setArguments(bundle);
	    return f;
	}
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, 
        Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.gallery_layout, container, false);
        return view;
    }

	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		String species = getArguments().getString(LATIN_SPECIES);
		new LoadPhotosOperation(getView()).execute(species);
	}
}
