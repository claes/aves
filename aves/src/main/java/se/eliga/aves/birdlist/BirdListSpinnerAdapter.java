/*
 * Copyright (c) Claes Holmerson 2013, 2015. The program is licensed under GNU GPL v3. See LICENSE.txt for details.
 */

package se.eliga.aves.birdlist;

import android.app.ActionBar;
import android.app.Activity;
import android.database.DataSetObserver;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import se.eliga.aves.R;
import se.eliga.aves.model.Bird;
import se.eliga.aves.model.DatabaseHandler;
import se.eliga.aves.model.Taxon;

/**
 * Created by Claes on 2015-05-17.
 */
public class BirdListSpinnerAdapter extends BirdListAdapter implements SpinnerAdapter {


    public BirdListSpinnerAdapter(Activity context, DatabaseHandler databaseHandler) {
        super(context, databaseHandler);
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Taxon taxon = getItem(position);
        if (taxon instanceof Bird) {
            Bird bird = (Bird) taxon;
            if (convertView == null
					/*|| convertView.getId() != R.layout.bird_row_layout*/) {
                LinearLayout linearLayout = new LinearLayout(context);
                linearLayout.setOrientation(LinearLayout.VERTICAL);
                linearLayout.setGravity(Gravity.LEFT | Gravity.FILL_HORIZONTAL);

                linearLayout.setPadding(9, 0, 9, 0);
                RelativeLayout.LayoutParams relativeParams = new RelativeLayout.LayoutParams(
                        ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.MATCH_PARENT);
                relativeParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
                TextView speciesView = new TextView(context);
                speciesView.setId(R.id.birdspecies_spinner_species);
                speciesView.setText(bird.getSwedishSpecies());
                speciesView.setTextSize(18);
                speciesView.setTextColor(context.getResources().getColor(
                        android.R.color.primary_text_dark));
                speciesView.setGravity(Gravity.LEFT);
                TextView familyView = new TextView(context);
                familyView.setId(R.id.birdspecies_spinner_order_family);
                familyView.setText(bird.getSwedishOrder() + " - " + bird.getSwedishFamily());
                familyView.setTextSize(10);
                familyView.setTextColor(context.getResources().getColor(
                        android.R.color.secondary_text_dark));
                familyView.setVisibility(View.VISIBLE);
                familyView.setSingleLine(true);
                familyView.setHorizontallyScrolling(true);
                linearLayout.addView(familyView);
                linearLayout.addView(speciesView);
                convertView = linearLayout;
            } else {
                TextView speciesView = (TextView) convertView.findViewById(R.id.birdspecies_spinner_species);
                speciesView.setTextSize(17);
                speciesView.setText(bird.getSwedishSpecies());
                TextView familyView = (TextView) convertView.findViewById(R.id.birdspecies_spinner_order_family);
                familyView.setVisibility(View.VISIBLE);
            }
        }
        return convertView;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        View view =  getView(position, convertView, parent);
        view.setMinimumHeight(65);
        view.findViewById(R.id.birdspecies_spinner_order_family).setVisibility(View.GONE);
        return view;
    }

    @Override
    public int getItemViewType(int i) {
        return 0;
    }

    @Override
    public void registerDataSetObserver(DataSetObserver dataSetObserver) {

    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver dataSetObserver) {

    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }
}
