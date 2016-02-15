package se.eliga.aves.facts;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.charts.ScatterChart;
import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.CombinedData;
import com.github.mikephil.charting.data.DataSet;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.ScatterData;
import com.github.mikephil.charting.data.ScatterDataSet;
import com.github.mikephil.charting.formatter.YAxisValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import se.eliga.aves.BirdApp;
import se.eliga.aves.Constants;
import se.eliga.aves.R;
import se.eliga.aves.birddetail.AbstractBirdSpeciesFragment;
import se.eliga.aves.model.Bird;
import se.eliga.aves.model.BirdFormatter;
import se.eliga.aves.model.DatabaseHandler;
import se.eliga.aves.model.County;
import se.eliga.aves.model.LocationStats;
import se.eliga.aves.model.ObsStats;

/**
 */
public class BirdSpeciesFactsFragment extends AbstractBirdSpeciesFragment {

    protected CombinedChart chart;

    private Map<String, MenuItem> countyMenuItems = new HashMap<String, MenuItem>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bird_facts_layout, container, false);
        setHasOptionsMenu(true);

        if (getSavedCountyId() == null) {
            CountyListDialogFragment dialog = new CountyListDialogFragment();
            dialog.setParentFragment(this);
            dialog.show(getFragmentManager(), "CountyListFragment");
        }

        return view;
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        loadBird(getCurrentBird());
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.facts_menu, menu);

        DatabaseHandler databaseHandler = ((BirdApp) getActivity().getApplication())
                .getDbHandler(getActivity());
        List<County> counties = databaseHandler.getCounties();


        SubMenu countyListItemSubMenu  = menu.findItem(R.id.county_list).getSubMenu();
        for (County county : counties) {
            MenuItem menuItem = countyListItemSubMenu.add(county.getName() + " län");
            countyMenuItems.put(county.getId(), menuItem);
            menuItem.setCheckable(true);
        }

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

        SharedPreferences settings = getActivity().getSharedPreferences(Constants.BIRD_APP_SETTINGS, 0);
        String selectedCountyId = settings.getString(Constants.SELECTED_COUNTY_ID, null);
        for (MenuItem menuItem : countyMenuItems.values()) {
            menuItem.setChecked(false);
        }
        MenuItem selectedMenuItem = countyMenuItems.get(selectedCountyId);
        if (selectedMenuItem != null) {
            selectedMenuItem.setChecked(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        Bird currentBird = getCurrentBird();
        String chosenCountyId = null;
        String chosenCountyName = null;
        for (String countyId : countyMenuItems.keySet()) {
            MenuItem countyItem = countyMenuItems.get(countyId);
            if (countyItem == item) {
                countyItem.setChecked(true);
                chosenCountyId = countyId;
                chosenCountyName = item.getTitle().toString();
            } else {
                countyItem.setChecked(false);
            }
        }
        if (chosenCountyId != null) {
            saveCountyId(chosenCountyId, chosenCountyName);
            loadBird(currentBird);
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    private void saveCountyId(String countyId, String countyName) {
        SharedPreferences settings = getActivity().getSharedPreferences(Constants.BIRD_APP_SETTINGS, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(Constants.SELECTED_COUNTY_ID, countyId);
        editor.putString(Constants.SELECTED_COUNTY_NAME, countyName);
        editor.commit();
    }

    private String getSavedCountyId() {
        SharedPreferences settings = getActivity().getSharedPreferences(Constants.BIRD_APP_SETTINGS, 0);
        String countyId = settings.getString(Constants.SELECTED_COUNTY_ID, null);
        return countyId;
    }

    private String getSavedCountyName() {
        SharedPreferences settings = getActivity().getSharedPreferences(Constants.BIRD_APP_SETTINGS, 0);
        String countyId = settings.getString(Constants.SELECTED_COUNTY_NAME, null);
        return countyId;
    }

    public void loadBirdInternal(Bird bird) {
        if (bird.getMinPopulationEstimate() >= 0 && bird.getMaxPopulationEstimate() >= 0 && bird.getBestPopulationEstimate() >= 0) {
            BirdFormatter birdFormatter = new BirdFormatter();
            TextView tv = (TextView) getView().findViewById(R.id.bestEstimate);
            tv.setText(birdFormatter.getFormattedPopulation(bird));
            tv.setTypeface(tv.getTypeface(), Typeface.BOLD);
            ((TextView) getView().findViewById(R.id.rangeEstimate)).setText("(" + birdFormatter.getFormattedPopulationRange(bird) + ")");
        } else {
            TextView tv = (TextView) getView().findViewById(R.id.bestEstimate);
            tv.setText("Estimat saknas");
            tv.setTypeface(tv.getTypeface(), Typeface.NORMAL);
            ((TextView) getView().findViewById(R.id.rangeEstimate)).setText("");
        }

        String countyName = getSavedCountyName();
        if (countyName != null) {
            ((TextView) getView().findViewById(R.id.observationsHeading)).setText(String.format("Observationer i %1$s", countyName));
            ((TextView) getView().findViewById(R.id.locationsHeading)).setText(String.format("Oftast observerad i %1$s", countyName));
            getView().findViewById(R.id.birdStatsCombinedchart).setVisibility(View.VISIBLE);
            initializeObservationsChart(bird);
            initializeLocationStats(bird);
        } else {
            getView().findViewById(R.id.birdStatsCombinedchart).setVisibility(View.INVISIBLE);
        }

    }

    private void initializeLocationStats(Bird bird) {
        String countyId = getSavedCountyId();
        if (countyId == null) {
            return;
        }
        DatabaseHandler databaseHandler = ((BirdApp) getActivity().getApplication())
                .getDbHandler(getActivity());

        List<LocationStats> stats = databaseHandler.getLocationStats(bird.getDyntaxaTaxonId(), countyId);

        TableLayout tl = (TableLayout) getView().findViewById(R.id.locationStatsTable);
        tl.removeAllViews();
        if (stats.size() > 0 ) {
            int i = 1;
            TableRow tr = null;
            for (final LocationStats locationStat : stats) {
                //Two column layout
                if ((i - 1) % 2 == 0) {
                    tr = new TableRow(getActivity());
                    tr.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
                    tl.addView(tr, new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));
                }

                TextView location = new TextView(getActivity());
                location.setText(i + ": " + locationStat.getLocality());
                location.setPadding(5, 2, 10, 2);
                location.setPaintFlags(location.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
                location.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
                location.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Uri geoLocation = Uri.parse("geo:" + locationStat.getLatitude() + "," + locationStat.getLongitude() + "?z=14");

                        //geoLocation = Uri.parse("http://maps.google.com/maps?q=" + locationStat.getLatitude() + ',' + locationStat.getLongitude()+ "("+ locationStat.getLocality() +")&z=15");
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setData(geoLocation);
                        intent.setPackage("com.google.android.apps.maps");
                        if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
                            startActivity(intent);
                        }
                    }
                });
                tr.addView(location);
                i++;
            }
        } else {
            TableRow tr = new TableRow(getActivity());
            tr.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
            tl.addView(tr, new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));
            TextView text  = new TextView(getActivity());
            text.setText("Uppgifter saknas");
            tr.addView(text);
        }
    }

    private void initializeObservationsChart(Bird bird) {

        String countyId = getSavedCountyId();
        if (countyId == null) {
            return;
        }

        BarData observationData = initBarChart(bird, countyId);
        ScatterData scatterData = initScatterChart(observationData);

        CombinedData data = new CombinedData(observationData.getXVals());
        data.setData(observationData);
        // Ugly way to exclude the "today" marker if set is
        // empty to in turn make no data available text appear
        if (!((DataSet) observationData.getDataSets().get(0)).getYVals().isEmpty()) {
            data.setData(scatterData);
        }

        chart.setData(data);
    }

    private ScatterData initScatterChart(BarData observationData) {
        ScatterData scatterData = new ScatterData(observationData.getXVals());
        ArrayList<Entry> entries = new ArrayList<Entry>();
        Calendar calendar = Calendar.getInstance();
        entries.add(new Entry(0f, (calendar.get(Calendar.DAY_OF_YEAR) - 1)/ 7));

        ScatterDataSet set = new ScatterDataSet(entries, "Idag");
        set.setColor(Color.RED);
        set.setScatterShape(ScatterChart.ScatterShape.TRIANGLE);
        set.setScatterShapeSize(10f);
        set.setDrawValues(false);
        set.setValueTextSize(15f);
        scatterData.addDataSet(set);
        return scatterData;
    }

    private BarData initBarChart(Bird bird, String countyId) {
        chart = (CombinedChart) getView().findViewById(R.id.birdStatsCombinedchart);

        chart.clear();
        chart.setNoDataText("Inga kända eller publika observationer");
        chart.setDescription(null);
        chart.setPinchZoom(false);
        chart.setDrawGridBackground(false);
        chart.setDoubleTapToZoomEnabled(false);
        chart.setScaleEnabled(false);
        chart.setDrawValueAboveBar(false);
        chart.setDrawBorders(false);
        chart.setDrawGridBackground(false);
        chart.getLegend().setTextColor(Color.WHITE);
        chart.setScaleXEnabled(true);
        chart.setDragEnabled(true);
        chart.setDrawHighlightArrow(false);

        BarChartMarkerView markerView =
                new BarChartMarkerView(getActivity(), R.layout.barchart_marker_layout);
        chart.setMarkerView(markerView);

        // X Axis
        XAxis xAxis = chart.getXAxis();
        xAxis.setDrawLabels(true);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setTextColor(Color.WHITE);
        xAxis.setLabelsToSkip(4);
        xAxis.setValueFormatter(null);

        // Y Axis
        YAxis leftAxis = chart.getAxisLeft();
        leftAxis.setTextColor(Color.WHITE);
        leftAxis.setDrawGridLines(false);
        leftAxis.setValueFormatter(new YAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, YAxis yAxis) {
                return "" + ((int) value);
            }
        });
        YAxis rightAxis = chart.getAxisRight();
        rightAxis.setEnabled(false);

        DatabaseHandler databaseHandler = ((BirdApp) getActivity().getApplication()).getDbHandler(getActivity());
        List<ObsStats> stats = databaseHandler.getObsStats(bird.getDyntaxaTaxonId(), countyId);
        return getObservationBarData(stats);
    }


    private BarData getObservationBarData(List<ObsStats> stats) {
        List<String> xVals = getXValues();
        ArrayList<BarEntry> yVals = getBarEntries(stats);
        ArrayList<BarDataSet> dataSets = getBarDataSets(yVals);

        BarData observationData = new BarData(xVals, dataSets);
        observationData.setValueTextSize(10f);
        return observationData;
    }

    private List<String> getXValues() {
        List<String> xVals = new ArrayList<String>();
        for (int i = 1; i <= 53; i++) {
            xVals.add("" + i);
        }
        return xVals;
    }

    private ArrayList<BarDataSet> getBarDataSets(ArrayList<BarEntry> yVals) {
        ArrayList<BarDataSet> dataSets = new ArrayList<BarDataSet>();
        BarDataSet observationDataSet = new BarDataSet(yVals, "Antal observationer 2000 - 2015");
        observationDataSet.setBarSpacePercent(35f);
        observationDataSet.setDrawValues(false);
        observationDataSet.setValueTextColor(Color.WHITE);
        dataSets.add(observationDataSet);
        return dataSets;
    }

    private ArrayList<BarEntry> getBarEntries(List<ObsStats> stats) {
        ArrayList<BarEntry> yVals = new ArrayList<BarEntry>();

        for (ObsStats obsStats : stats) {
            yVals.add(new BarEntry(obsStats.getObservations(), obsStats.getWeek() - 1 ));
        }
        return yVals;
    }

    public class BarChartMarkerView extends MarkerView {

        private TextView dateMarkerText;
        private TextView valueMarkerText;

        public BarChartMarkerView (Context context, int layoutResource) {
            super(context, layoutResource);
            dateMarkerText = (TextView) findViewById(R.id.barChartDateDescriptor);
            valueMarkerText = (TextView) findViewById(R.id.barChartValueDescriptor);
        }

        // callbacks everytime the MarkerView is redrawn, can be used to update the
        // content (user-interface)
        @Override
        public void refreshContent(Entry e, Highlight highlight) {
            dateMarkerText.setText(getFormattedDatePeriod(e.getXIndex()));
            valueMarkerText.setText(("" + (int) e.getVal()));
        }

        @Override
        public int getXOffset() {
            return -(getWidth() / 2);
        }

        @Override
        public int getYOffset() {
            return -getHeight();
        }

        public String getFormattedDatePeriod(int xIndex) {
            int firstDayNo = (xIndex) * 7 + 1; //xIndex starts from 0
            int lastDayNo = firstDayNo + 6;
            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.DAY_OF_YEAR, firstDayNo);
            cal.set(Calendar.YEAR, 2016);
            Date firstDate = cal.getTime();
            cal.set(Calendar.DAY_OF_YEAR, lastDayNo);
            Date lastDate = cal.getTime();
            SimpleDateFormat sdf = new SimpleDateFormat("d/M");
            return sdf.format(firstDate) + " - " + sdf.format(lastDate);
        }
    }
}
