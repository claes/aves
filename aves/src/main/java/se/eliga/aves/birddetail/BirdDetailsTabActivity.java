/*
 * Copyright (c) Claes Holmerson 2013, 2015. The program is licensed under GNU GPL v3. See LICENSE.txt for details.
 */

package se.eliga.aves.birddetail;

import android.app.ActionBar;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTabHost;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TabWidget;

import se.eliga.aves.BirdApp;
import se.eliga.aves.R;
import se.eliga.aves.birdlist.BirdListFragment;
import se.eliga.aves.birdlist.BirdListSpinnerAdapter;
import se.eliga.aves.maps.BirdSpeciesOccurrencesGBIFMapFragment;
import se.eliga.aves.model.Bird;
import se.eliga.aves.model.DatabaseHandler;
import se.eliga.aves.photos.BirdSpeciesFlickrGalleryFragment;
import se.eliga.aves.songs.BirdSpeciesXenoCantoPlayerFragment;

public class BirdDetailsTabActivity extends FragmentActivity {

	private FragmentTabHost tabHost;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.bird_details_tab_layout);

		final String latinSpecies = getIntent().getExtras().getString(
				BirdListFragment.LATIN_SPECIES);
		final String englishSpecies = getIntent().getExtras().getString(
				BirdListFragment.ENGLISH_SPECIES);
		final String swedishFamily = getIntent().getExtras().getString(
				BirdListFragment.SWEDISH_FAMILY);


		Bundle bundle = new Bundle(1);
		bundle.putString(BirdSpeciesWikipediaFragment.ENGLISH_SPECIES,
				englishSpecies);
		bundle.putString(BirdSpeciesXenoCantoPlayerFragment.LATIN_SPECIES,
				latinSpecies);

		tabHost = (FragmentTabHost) findViewById(android.R.id.tabhost);
		tabHost.setup(this, getSupportFragmentManager(), R.id.tabFrameLayout);


		tabHost.addTab(
				tabHost.newTabSpec("Wikipedia").setIndicator("Wikipedia",
						getResources().getDrawable(android.R.drawable.star_on)),
				BirdSpeciesWikipediaFragment.class, bundle);
		tabHost.addTab(
				tabHost.newTabSpec("Flickr").setIndicator("Flickr",
						getResources().getDrawable(android.R.drawable.star_on)),
				BirdSpeciesFlickrGalleryFragment.class, bundle);
		tabHost.addTab(
				tabHost.newTabSpec("xeno-canto").setIndicator("xeno-canto",
						getResources().getDrawable(android.R.drawable.star_on)),
				BirdSpeciesXenoCantoPlayerFragment.class, bundle);

		tabHost.addTab(
				tabHost.newTabSpec("Karta").setIndicator("Karta",
						getResources().getDrawable(android.R.drawable.star_on)),
				BirdSpeciesOccurrencesGBIFMapFragment.class, bundle);

		TabWidget tabWidget = (TabWidget) findViewById(android.R.id.tabs);
		LinearLayout linearLayout = (LinearLayout) tabWidget.getParent();
		HorizontalScrollView horizontalScrollView = new HorizontalScrollView(
				this);
		horizontalScrollView.setLayoutParams(new FrameLayout.LayoutParams(
				FrameLayout.LayoutParams.MATCH_PARENT,
				FrameLayout.LayoutParams.WRAP_CONTENT));
		linearLayout.addView(horizontalScrollView, 0);
		linearLayout.removeView(tabWidget);
		horizontalScrollView.addView(tabWidget);
		horizontalScrollView.setHorizontalScrollBarEnabled(false);

		BirdListSpinnerAdapter birdListSpinnerAdapter = createAdapter();
		birdListSpinnerAdapter.setFilterFamily(swedishFamily);
		birdListSpinnerAdapter.refresh();
		birdListSpinnerAdapter.notifyDataSetChanged();

		Spinner spinner = new Spinner(this);
		spinner.setId(R.id.birdspecies_spinner);
		spinner.setAdapter(birdListSpinnerAdapter);
		spinner.setGravity(Gravity.FILL_HORIZONTAL);

		spinner.setSelection(birdListSpinnerAdapter.getPosition(new Bird(latinSpecies)));
		spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
				BirdSpeciesFragment fragment = (BirdSpeciesFragment) getSupportFragmentManager().findFragmentByTag(tabHost.getCurrentTabTag());
				Spinner spinner = (Spinner) getActionBar().getCustomView().findViewById(R.id.birdspecies_spinner);
				fragment.loadBird((Bird) spinner.getSelectedItem());
			}

			@Override
			public void onNothingSelected(AdapterView<?> adapterView) {
			}
		});
		getActionBar().setCustomView(spinner);
		getActionBar().setDisplayOptions(ActionBar.DISPLAY_HOME_AS_UP
				| ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_SHOW_CUSTOM);

	}

	public BirdListSpinnerAdapter createAdapter() {
		DatabaseHandler databaseHandler = ((BirdApp) getApplication())
				.getDbHandler();
		return new BirdListSpinnerAdapter(this, databaseHandler);
	}

}