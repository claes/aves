/*
 * Copyright (c) Claes Holmerson 2013, 2015. The program is licensed under GNU GPL v3. See LICENSE.txt for details.
 */

package se.eliga.aves.birddetail;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTabHost;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TabWidget;

import java.net.URLEncoder;

import se.eliga.aves.BirdApp;
import se.eliga.aves.Constants;
import se.eliga.aves.R;
import se.eliga.aves.birdlist.BirdListAdapter;
import se.eliga.aves.birdlist.BirdListFragment;
import se.eliga.aves.birdlist.BirdListSpinnerAdapter;
import se.eliga.aves.maps.MapFragment;
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
		bundle.putString(BirdSpeciesWebFragment.ENGLISH_SPECIES,
				englishSpecies);
		bundle.putString(BirdSpeciesXenoCantoPlayerFragment.LATIN_SPECIES,
				latinSpecies);

		tabHost = (FragmentTabHost) findViewById(android.R.id.tabhost);
		tabHost.setup(this, getSupportFragmentManager(), R.id.tabFrameLayout);


		tabHost.addTab(
				tabHost.newTabSpec("Wikipedia").setIndicator("Wikipedia",
						getResources().getDrawable(android.R.drawable.star_on)),
				BirdSpeciesWebFragment.class, bundle);
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
				MapFragment.class, bundle);

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
		birdListSpinnerAdapter.initialize(getSharedPreferences(Constants.BIRD_APP_SETTINGS, Context.MODE_PRIVATE));
		if (BirdListAdapter.SortOption.PHYLOGENETIC.equals(birdListSpinnerAdapter.getSortOption())) {
			birdListSpinnerAdapter.setSortOption(BirdListAdapter.SortOption.SWEDISH);
		}

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

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.detail_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Bird currentBird = getCurrentBird();
		Intent intent = new Intent(Intent.ACTION_VIEW);
		String url;
		switch (item.getItemId()) {
			case R.id.artportalen_sightings:
				url = getArtportalenSightingsUrl(currentBird);
				break;
			case R.id.artportalen_photos:
				url = getArtportalenPhotosUrl(currentBird);
				break;
			case R.id.artfakta:
				url = getArtfaktaUrl(currentBird);
				break;
			case R.id.google_photos:
				url = getGoogleImagesUrl(currentBird);
				break;
			case R.id.youtube:
				url = getYoutubeUrl(currentBird);
				break;
			default:
				return super.onOptionsItemSelected(item);
		}
		intent.setData(Uri.parse(url));
		startActivity(intent);
		return true;
	}

	public BirdListSpinnerAdapter createAdapter() {
		DatabaseHandler databaseHandler = ((BirdApp) getApplication())
				.getDbHandler();
		return new BirdListSpinnerAdapter(this, databaseHandler);
	}

	public Bird getCurrentBird() {
		Spinner spinner  = (Spinner) getActionBar().getCustomView().findViewById(R.id.birdspecies_spinner);
		return (Bird) spinner.getSelectedItem();
	}

	protected String getArtportalenSightingsUrl(Bird bird) {
		return "http://artportalen.se/Mobile/Sightings/Observerad/Alla%20arter/"+bird.getDyntaxaTaxonId();
	}

	protected String getArtportalenPhotosUrl(Bird bird) {
		return "http://artportalen.se/Mobile/TaxonGallery/"+bird.getDyntaxaTaxonId()+"/1";
	}

	protected String getArtfaktaUrl(Bird bird) {
		return "http://artfakta.artdatabanken.se/taxon/" + bird.getDyntaxaTaxonId() + "#presentationArea";
	}

	protected String getGoogleImagesUrl(Bird bird) {
		return "https://www.google.com/search?tbm=isch&q=" + Uri.encode(bird.getLatinSpecies()) + "#rcnt,  ";
	}

	protected String getYoutubeUrl(Bird bird) {
		return "https://www.youtube.com/results?search_query=" + Uri.encode(bird.getLatinSpecies());
	}


}