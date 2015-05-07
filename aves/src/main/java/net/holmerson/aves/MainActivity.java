package net.holmerson.aves;

import android.os.Bundle;
import android.os.StrictMode;
import android.app.ActionBar;
import android.app.ListActivity;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

public class MainActivity extends ListActivity {

	public static final String LATIN_SPECIES = "latinSpecies";
	public static final String ENGLISH_SPECIES = "englishSpecies";
	public static final String SWEDISH_SPECIES = "swedishSpecies";
	public static final String SWEDISH_FAMILY = "swedishFamily";
	public static final String SWEDISH_ORDER = "swedishOrder";

	ImageLoader imageLoader = ImageLoader.getInstance();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		imageLoader.init(ImageLoaderConfiguration.createDefault(this));
		//setContentView(R.layout.activity_main);
		
		ListView listView = getListView();
		listView.setFastScrollEnabled(true);

		//
		// BirdListLoader loader = new BirdListLoader(this);
		//
		// ItemManager.Builder builder = new ItemManager.Builder(loader);
		// builder.setPreloadItemsEnabled(true).setPreloadItemsCount(5);
		// builder.setThreadPoolSize(4);
		// ItemManager itemManager = builder.build();
		//
		// AsyncListView listView = (AsyncListView) findViewById(R.id.listView);
		// listView.setItemManager(itemManager);
		//

		setListAdapter(createAdapter());
	}

	private MenuItem breedingItem;
	private MenuItem breedingUnclearItem;
	private MenuItem rareItem;
	private MenuItem unseenItem;
	private MenuItem migrantItem;
	private MenuItem regularVisitorItem;
	private MenuItem nonSpontaneousItem;

	private MenuItem swedishSortItem;
	private MenuItem englishSortItem;
	private MenuItem latinSortItem;
	private MenuItem phylogeneticSortItem;

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main_menu, menu);
		return true;
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		BirdListAdapter birdListAdapter = (BirdListAdapter) getListAdapter();

		// Filtering
		breedingItem = menu.findItem(R.id.breeding);
		breedingItem.setChecked(birdListAdapter.isShowBreeding());
		breedingUnclearItem = menu.findItem(R.id.breeding_unclear);
		breedingUnclearItem.setChecked(birdListAdapter.isShowBreedingUnclear());
		migrantItem = menu.findItem(R.id.migrant);
		migrantItem.setChecked(birdListAdapter.isShowMigrant());
		unseenItem = menu.findItem(R.id.unseen);
		unseenItem.setChecked(birdListAdapter.isShowUnseen());
		regularVisitorItem = menu.findItem(R.id.regular_visitor);
		regularVisitorItem.setChecked(birdListAdapter.isShowRegularVisitor());
		nonSpontaneousItem = menu.findItem(R.id.non_spontaneous);
		nonSpontaneousItem.setChecked(birdListAdapter.isShowNonSpontaneous());
		rareItem = menu.findItem(R.id.rare);
		rareItem.setChecked(birdListAdapter.isShowRare());

		// Sorting
		swedishSortItem = menu.findItem(R.id.alphabetic_swedish);
		swedishSortItem.setChecked(birdListAdapter.getSortOption().equals(
				BirdListAdapter.SortOption.SWEDISH));
		englishSortItem = menu.findItem(R.id.alphabetic_english);
		englishSortItem.setChecked(birdListAdapter.getSortOption().equals(
				BirdListAdapter.SortOption.ENGLISH));
		latinSortItem = menu.findItem(R.id.alphabetic_latin);
		latinSortItem.setChecked(birdListAdapter.getSortOption().equals(
				BirdListAdapter.SortOption.SCIENTIFIC));
		phylogeneticSortItem = menu.findItem(R.id.phylogenic_sort);
		phylogeneticSortItem.setChecked(birdListAdapter.getSortOption().equals(
				BirdListAdapter.SortOption.PHYLOGENETIC));
		
		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		if (item.isCheckable()) {

			if (R.id.filtering_group == item.getGroupId()) {
				item.setChecked(!item.isChecked());
			} else if (R.id.sort_order_group == item.getGroupId()) {
				swedishSortItem.setChecked(false);
				englishSortItem.setChecked(false);
				latinSortItem.setChecked(false);
				phylogeneticSortItem.setChecked(false);
				item.setChecked(true);
			}

			BirdListAdapter birdListAdapter = (BirdListAdapter) getListAdapter();
			switch (item.getItemId()) {
			case R.id.alphabetic_latin:
				birdListAdapter
						.setSortOption(BirdListAdapter.SortOption.SCIENTIFIC);
				break;
			case R.id.alphabetic_swedish:
				birdListAdapter
						.setSortOption(BirdListAdapter.SortOption.SWEDISH);
				break;
			case R.id.alphabetic_english:
				birdListAdapter
						.setSortOption(BirdListAdapter.SortOption.ENGLISH);
				break;
			case R.id.phylogenic_sort:
				birdListAdapter
						.setSortOption(BirdListAdapter.SortOption.PHYLOGENETIC);
				break;

			}

			birdListAdapter.setShowBreeding(breedingItem.isChecked());
			birdListAdapter.setShowBreedingUnclear(breedingUnclearItem
					.isChecked());
			birdListAdapter.setShowMigrant(migrantItem.isChecked());
			birdListAdapter.setShowRare(rareItem.isChecked());
			birdListAdapter.setShowRegularVisitor(regularVisitorItem
					.isChecked());
			birdListAdapter.setShowUnseen(unseenItem.isChecked());
			birdListAdapter.setShowNonSpontaneous(nonSpontaneousItem
					.isChecked());

			birdListAdapter.refresh();
			birdListAdapter.notifyDataSetChanged();
		}
		return true;
	}

	public ListAdapter createAdapter() {
		DatabaseHandler databaseHandler = ((BirdApplication) getApplication())
				.getDbHandler();
		return new BirdListAdapter(this, databaseHandler);
	}

	public BirdListAdapter getBirdListAdapter() {
		return (BirdListAdapter) getListAdapter();
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		Taxon taxon = getBirdListAdapter().getItem(position);
		if (taxon instanceof Bird) {
			Intent intent = new Intent(this, BirdDetailsTabActivity.class);
			Bundle bundle = new Bundle();
			bundle.putString(LATIN_SPECIES, ((Bird) taxon).getLatinSpecies()); 																				
			bundle.putString(ENGLISH_SPECIES, ((Bird) taxon).getEnglishSpecies()); 																				
			bundle.putString(SWEDISH_SPECIES, ((Bird) taxon).getSwedishSpecies()); 																				
			bundle.putString(SWEDISH_FAMILY, ((Bird) taxon).getSwedishFamily());
			bundle.putString(SWEDISH_ORDER, ((Bird) taxon).getSwedishOrder());
			intent.putExtras(bundle);
			startActivity(intent);
		}
	}
}