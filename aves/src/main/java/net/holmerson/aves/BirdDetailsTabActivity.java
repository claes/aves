package net.holmerson.aves;

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

public class BirdDetailsTabActivity extends FragmentActivity {

	private FragmentTabHost tabHost;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.bird_details_tab_layout);

		final String latinSpecies = getIntent().getExtras().getString(
				BirdListActivity.LATIN_SPECIES);
		final String englishSpecies = getIntent().getExtras().getString(
				BirdListActivity.ENGLISH_SPECIES);
		final String swedishSpecies = getIntent().getExtras().getString(
				BirdListActivity.SWEDISH_SPECIES);
		final String swedishFamily = getIntent().getExtras().getString(
				BirdListActivity.SWEDISH_FAMILY);
		final String swedishOrder = getIntent().getExtras().getString(
				BirdListActivity.SWEDISH_ORDER);

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

			getActionBar().setCustomView(linearLayout);
			getActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
		}

		Bundle bundle = new Bundle(1);
		bundle.putString(AbstractBirdSpeciesWikipediaFragment.ENGLISH_SPECIES,
				englishSpecies);
		bundle.putString(BirdSpeciesXenoCantoPlayerFragment.LATIN_SPECIES,
				latinSpecies);

		tabHost = (FragmentTabHost) findViewById(android.R.id.tabhost);
		tabHost.setup(this, getSupportFragmentManager(), R.id.tabFrameLayout);

		tabHost.addTab(
				tabHost.newTabSpec("Wikipedia (sv)").setIndicator(
						"Wikipedia (sv)",
						getResources().getDrawable(android.R.drawable.star_on)),
				BirdSpeciesWikipediaSwedishFragment.class, bundle);
		tabHost.addTab(
				tabHost.newTabSpec("Wikipedia (en)").setIndicator(
						"Wikipedia (en)",
						getResources().getDrawable(android.R.drawable.star_on)),
				BirdSpeciesWikipediaEnglishFragment.class, bundle);
		tabHost.addTab(
				tabHost.newTabSpec("Flickr").setIndicator("Flickr",
						getResources().getDrawable(android.R.drawable.star_on)),
				BirdSpeciesFlickrGalleryFragment.class, bundle);
		tabHost.addTab(
				tabHost.newTabSpec("xeno-canto").setIndicator("xeno-canto",
						getResources().getDrawable(android.R.drawable.star_on)),
				BirdSpeciesXenoCantoPlayerFragment.class, bundle);

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