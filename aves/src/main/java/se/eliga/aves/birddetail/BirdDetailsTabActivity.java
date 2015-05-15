/*
 * Copyright (c) Claes Holmerson 2013, 2015. The program is licensed under GNU GPL v3. See LICENSE.txt for details.
 */

package se.eliga.aves.birddetail;

import android.app.ActionBar;
import android.app.ActionBar.LayoutParams;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTabHost;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TabWidget;
import android.widget.TextView;

import se.eliga.aves.R;
import se.eliga.aves.birdlist.BirdListFragment;
import se.eliga.aves.maps.BirdSpeciesOccurrencesGBIFMapFragment;
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
		final String swedishSpecies = getIntent().getExtras().getString(
				BirdListFragment.SWEDISH_SPECIES);
		final String swedishFamily = getIntent().getExtras().getString(
				BirdListFragment.SWEDISH_FAMILY);
		final String swedishOrder = getIntent().getExtras().getString(
				BirdListFragment.SWEDISH_ORDER);

		{
			LinearLayout linearLayout = new LinearLayout(this);
			linearLayout.setOrientation(LinearLayout.VERTICAL);
			linearLayout.setGravity(Gravity.LEFT);
			linearLayout.setPadding(9, 1, 9, 1);
			RelativeLayout.LayoutParams relativeParams = new RelativeLayout.LayoutParams(
					LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
			relativeParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
			TextView speciesView = new TextView(this);
			speciesView.setId(1);
			speciesView.setText(swedishSpecies);
			speciesView.setTextSize(20);
			speciesView.setTextColor(getResources().getColor(
					android.R.color.primary_text_dark));
			speciesView.setGravity(Gravity.LEFT);
			TextView familyView = new TextView(this);
			familyView.setId(2);
			familyView.setText(swedishOrder + " - " + swedishFamily);
			familyView.setTextColor(getResources().getColor(
					android.R.color.secondary_text_dark));
			linearLayout.addView(familyView);
			linearLayout.addView(speciesView);

			/*
			ArrayList<String> spinnerArray = new ArrayList<String>();
			spinnerArray.add("1");
			spinnerArray.add("2");
			Spinner spinner = new Spinner(this);
			ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(this,
					android.R.layout.simple_spinner_dropdown_item,
					spinnerArray);
			spinner.setAdapter(spinnerArrayAdapter);
			spinner.addView(linearLayout);
			*/

			getActionBar().setCustomView(linearLayout);
			getActionBar().setDisplayOptions(ActionBar.DISPLAY_HOME_AS_UP
					| ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_SHOW_CUSTOM);
		}

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

		{
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
		}

	}

}