/*
 * Copyright (c) Claes Holmerson 2013, 2015. The program is licensed under GNU GPL v3. See LICENSE.txt for details.
 */

package se.eliga.aves.birddetail;

import android.support.v4.app.Fragment;
import android.widget.Spinner;

import se.eliga.aves.R;
import se.eliga.aves.model.Bird;

/**
 * Created by Claes on 2015-05-17.
 */
public abstract class AbstractBirdSpeciesFragment extends Fragment implements BirdSpeciesFragment {

    public Bird getCurrentBird() {
        Spinner spinner  = (Spinner) getActivity().getActionBar().getCustomView().findViewById(R.id.birdspecies_spinner);
        Bird bird = (Bird) spinner.getSelectedItem();
        return bird;
    }

    public final void loadBird(Bird bird) {
        int redlistImageResource = getActivity().getResources().getIdentifier("redlist_" +
                        bird.getSwedishRedlistCategory().getText().toLowerCase(),
                "drawable", getActivity().getApplicationInfo().packageName);
        //Use IUCN icon if can be determined.
        int icon = redlistImageResource > 0 ? redlistImageResource : R.mipmap.ic_launcher;
        getActivity().getActionBar().setIcon(icon);
        loadBirdInternal(bird);
    }

    public abstract void loadBirdInternal(Bird bird);

}
