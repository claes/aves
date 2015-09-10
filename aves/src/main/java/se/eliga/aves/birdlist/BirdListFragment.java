/*
 * Copyright (c) Claes Holmerson 2013, 2015. The program is licensed under GNU GPL v3. See LICENSE.txt for details.
 */

package se.eliga.aves.birdlist;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.view.MenuItemCompat;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.SearchView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import se.eliga.aves.BirdApp;
import se.eliga.aves.birddetail.BirdDetailsTabActivity;
import se.eliga.aves.Constants;
import se.eliga.aves.MainActivity;
import se.eliga.aves.R;
import se.eliga.aves.model.DatabaseHandler;
import se.eliga.aves.model.Taxon;
import se.eliga.aves.model.Bird;

/**
 * Created by Claes on 2015-05-08.
 */
public class BirdListFragment extends ListFragment {

    public static final String LATIN_SPECIES = "latinSpecies";
    public static final String ENGLISH_SPECIES = "englishSpecies";
    public static final String SWEDISH_SPECIES = "swedishSpecies";
    public static final String SWEDISH_FAMILY = "swedishFamily";
    public static final String SWEDISH_ORDER = "swedishOrder";

    ImageLoader imageLoader = ImageLoader.getInstance();

    private MenuItem breedingItem;
    private MenuItem breedingUnclearItem;
    private MenuItem rareItem;
    private MenuItem unseenItem;
    private MenuItem unclassifiedItem;
    private MenuItem migrantItem;
    private MenuItem regularVisitorItem;
    private MenuItem nonSpontaneousItem;

    private MenuItem swedishSortItem;
    private MenuItem englishSortItem;
    private MenuItem latinSortItem;
    private MenuItem phylogeneticSortItem;
    private MenuItem populationSortItem;

    private String filterString;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        imageLoader.init(ImageLoaderConfiguration.createDefault(getActivity()));
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ListView listView = getListView();
        listView.setFastScrollEnabled(true);

        BirdListAdapter birdListAdapter = createAdapter();
        birdListAdapter.initialize(getActivity().getSharedPreferences(Constants.BIRD_APP_SETTINGS, Context.MODE_PRIVATE));
        birdListAdapter.refresh();
        birdListAdapter.notifyDataSetChanged();
        setListAdapter(birdListAdapter);
        setHasOptionsMenu(true);
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.main_menu, menu);

        MenuItem item = menu.findItem(R.id.species_search);
        SearchView searchView = new SearchView(((MainActivity) getActivity()).getActionBar().getThemedContext());
        MenuItemCompat.setShowAsAction(item, MenuItemCompat.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW | MenuItemCompat.SHOW_AS_ACTION_IF_ROOM);
        MenuItemCompat.setActionView(item, searchView);
        final BirdListAdapter birdListAdapter = (BirdListAdapter) getListAdapter();
        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                birdListAdapter.setFilterString(null);
                birdListAdapter.refresh();
                birdListAdapter.notifyDataSetChanged();
                return false;
            }
        });
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                birdListAdapter.setFilterString(query);
                birdListAdapter.refresh();
                birdListAdapter.notifyDataSetChanged();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                birdListAdapter.setFilterString(newText);
                birdListAdapter.refresh();
                birdListAdapter.notifyDataSetChanged();
                return true;
            }
        });
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
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
        unclassifiedItem = menu.findItem(R.id.unclassified);
        unclassifiedItem.setChecked(birdListAdapter.isShowUnclassified());
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
        populationSortItem = menu.findItem(R.id.population_sort);
        populationSortItem.setChecked(birdListAdapter.getSortOption().equals(
                BirdListAdapter.SortOption.POPULATION));

    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        SharedPreferences settings = getActivity().getSharedPreferences(Constants.BIRD_APP_SETTINGS, 0);
        saveFilterPreferences(settings);
        super.onSaveInstanceState(savedInstanceState);
    }

    private void saveFilterPreferences(SharedPreferences settings) {
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean(Constants.BIRD_LIST_SHOW_BREEDING, getBirdListAdapter().isShowBreeding());
        editor.putBoolean(Constants.BIRD_LIST_SHOW_BREEDING_UNCLEAR, getBirdListAdapter().isShowBreedingUnclear());
        editor.putBoolean(Constants.BIRD_LIST_SHOW_MIGRANT, getBirdListAdapter().isShowMigrant());
        editor.putBoolean(Constants.BIRD_LIST_SHOW_NON_SPONTANEOUS, getBirdListAdapter().isShowNonSpontaneous());
        editor.putBoolean(Constants.BIRD_LIST_SHOW_RARE, getBirdListAdapter().isShowRare());
        editor.putBoolean(Constants.BIRD_LIST_SHOW_UNSEEN, getBirdListAdapter().isShowUnseen());
        editor.putBoolean(Constants.BIRD_LIST_SHOW_UNCLASSIFIED, getBirdListAdapter().isShowUnclassified());
        editor.putString(Constants.BIRD_LIST_SHOW_OPTION, getBirdListAdapter().getSortOption().getCode());
        editor.commit();
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
                populationSortItem.setChecked(false);
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
                case R.id.population_sort:
                    birdListAdapter
                            .setSortOption(BirdListAdapter.SortOption.POPULATION);
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
            birdListAdapter.setShowUnclassified(unclassifiedItem.isChecked());
            birdListAdapter.setShowNonSpontaneous(nonSpontaneousItem
                    .isChecked());

            birdListAdapter.refresh();
            birdListAdapter.notifyDataSetChanged();
        }
        SharedPreferences settings = getActivity().getSharedPreferences(Constants.BIRD_APP_SETTINGS, 0);
        saveFilterPreferences(settings);
        return true;
    }


    public BirdListAdapter createAdapter() {
        DatabaseHandler databaseHandler = ((BirdApp) getActivity().getApplication())
                .getDbHandler();
        return new BirdListAdapter(getActivity(), databaseHandler);
    }

    public BirdListAdapter getBirdListAdapter() {
        return (BirdListAdapter) getListAdapter();
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        Taxon taxon = getBirdListAdapter().getItem(position);
        if (taxon instanceof Bird) {
            Intent intent = new Intent(getActivity(), BirdDetailsTabActivity.class);
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
