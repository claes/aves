package se.eliga.aves.facts;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.XAxisValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import se.eliga.aves.BirdApp;
import se.eliga.aves.Constants;
import se.eliga.aves.R;
import se.eliga.aves.birddetail.AbstractBirdSpeciesFragment;
import se.eliga.aves.model.Bird;
import se.eliga.aves.model.BirdFormatter;
import se.eliga.aves.model.DatabaseHandler;
import se.eliga.aves.model.LocationStats;
import se.eliga.aves.model.ObsStats;

/**
 */
public class BirdSpeciesFactsFragment extends AbstractBirdSpeciesFragment {


    protected BarChart chart;

    private MenuItem menuItemMonthlyStats;
    private MenuItem menuItemWeeklyStats;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bird_facts_layout, container, false);
        setHasOptionsMenu(true);
        return view;
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        loadBird(getCurrentBird());
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.facts_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        menuItemMonthlyStats = menu.findItem(R.id.stats_monthly);
        menuItemWeeklyStats = menu.findItem(R.id.stats_weekly);
        SharedPreferences settings = getActivity().getSharedPreferences(Constants.BIRD_APP_SETTINGS, 0);
        StatsType statsType = StatsType.lookupByCode(settings.getString(Constants.STATS_TYPE, StatsType.STATS_MONTHLY.getCode()));
        switch (statsType) {
            case STATS_MONTHLY:
                menuItemMonthlyStats.setChecked(true);
                menuItemWeeklyStats.setChecked(false);
                break;
            case STATS_WEEKLY:
                menuItemMonthlyStats.setChecked(false);
                menuItemWeeklyStats.setChecked(true);
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Bird currentBird = getCurrentBird();
        switch (item.getItemId()) {
            case R.id.stats_weekly:
                menuItemMonthlyStats.setChecked(false);
                menuItemWeeklyStats.setChecked(true);
                saveStatsType(StatsType.STATS_WEEKLY);
                loadBird(currentBird);
                break;
            case R.id.stats_monthly:
                menuItemMonthlyStats.setChecked(true);
                menuItemWeeklyStats.setChecked(false);
                saveStatsType(StatsType.STATS_MONTHLY);
                loadBird(currentBird);
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    private void saveStatsType(StatsType statsType) {
        SharedPreferences settings = getActivity().getSharedPreferences(Constants.BIRD_APP_SETTINGS, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(Constants.STATS_TYPE, statsType.getCode());
        editor.commit();
    }

    public void loadBirdInternal(Bird bird) {

        if (bird.getMinPopulationEstimate() >= 0 && bird.getMaxPopulationEstimate() >= 0 && bird.getBestPopulationEstimate() >= 0) {
            BirdFormatter birdFormatter = new BirdFormatter();
            ((TextView) getView().findViewById(R.id.bestEstimate)).setText(birdFormatter.getFormattedPopulation(bird));
            ((TextView) getView().findViewById(R.id.rangeEstimate)).setText("(" + birdFormatter.getFormattedPopulationRange(bird) + ")");
        } else {
            ((TextView) getView().findViewById(R.id.bestEstimate)).setText("");
            ((TextView) getView().findViewById(R.id.rangeEstimate)).setText("Estimat saknas");
        }

        initializeObservationsChart(bird);
        initializeLocationStats(bird);
    }

    private void initializeLocationStats(Bird bird) {
        DatabaseHandler databaseHandler = ((BirdApp) getActivity().getApplication())
                .getDbHandler();
        List<LocationStats> stats = databaseHandler.getLocationStats(bird.getDyntaxaTaxonId(), "AB");

        TableLayout tl = (TableLayout) getView().findViewById(R.id.locationStatsTable);
        tl.removeAllViews();
        int i = 1;
        TableRow tr = null;
        for (LocationStats locationStat : stats) {
            //Two column layout
            if ((i-1) % 2 == 0) {
                tr = new TableRow(getActivity());
                tr.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
                tl.addView(tr, new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));
            }
            TextView location = new TextView(getActivity());
            location.setText(i + ": " + locationStat.getLocality());
            location.setPadding(5, 2, 10, 2);
            location.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
            tr.addView(location);
            i++;
        }
    }

    private void initializeObservationsChart(Bird bird) {

        SharedPreferences settings = getActivity().getSharedPreferences(Constants.BIRD_APP_SETTINGS, 0);
        StatsType statsType = StatsType.lookupByCode(settings.getString(Constants.STATS_TYPE, StatsType.STATS_MONTHLY.getCode()));

        chart = (BarChart) getView().findViewById(R.id.birdStatsBarchart);
        initBarChart(statsType);

        DatabaseHandler databaseHandler = ((BirdApp) getActivity().getApplication()).getDbHandler();
        List<ObsStats> stats = databaseHandler.getObsStats(bird.getDyntaxaTaxonId(), "AB", statsType.getCode());
        BarData observationData = getObservationBarData(statsType, stats);

        chart.setData(observationData);
    }

    private BarData getObservationBarData(StatsType statsType, List<ObsStats> stats) {
        List<String> xVals = getXValues(statsType);
        ArrayList<BarEntry> yVals = getBarEntries(statsType, stats);
        ArrayList<BarDataSet> dataSets = getBarDataSets(yVals);

        BarData observationData = new BarData(xVals, dataSets);
        observationData.setValueTextSize(10f);
        return observationData;
    }

    private void initBarChart(StatsType statsType) {
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

        BarChartMarkerView markerView =
                new BarChartMarkerView (getActivity(), R.layout.barchart_marker_layout, statsType);
        chart.setMarkerView(markerView);

        // X Axis
        XAxis xAxis = chart.getXAxis();
        xAxis.setDrawLabels(true);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setTextColor(Color.WHITE);


        // Y Axis
        YAxis leftAxis = chart.getAxisLeft();
        leftAxis.setTextColor(Color.WHITE);
        leftAxis.setDrawGridLines(false);
        YAxis rightAxis = chart.getAxisRight();
        rightAxis.setEnabled(false);

        if (StatsType.STATS_MONTHLY.equals(statsType)) {
            xAxis.setLabelsToSkip(0);
            xAxis.setValueFormatter(new XAxisValueFormatter() {
                private String[] labels = {"J", "F", "M", "A", "M", "J", "J", "A", "S", "O", "N", "D"};

                @Override
                public String getXValue(String original, int index, ViewPortHandler viewPortHandler) {
                    return labels[index];
                }
            });
        } else {
            xAxis.setLabelsToSkip(4);
            xAxis.setValueFormatter(null);
        }

    }

    private List<String> getXValues(StatsType statsType) {
        List<String> xVals = new ArrayList<String>();
        if (StatsType.STATS_MONTHLY.equals(statsType)) {
            for (int i = 1; i <= 12; i++) {
                xVals.add("" + i);
            }
        } else {
            for (int i = 1; i <= 53; i++) {
                xVals.add("" + i);
            }
        }
        return xVals;
    }

    private ArrayList<BarDataSet> getBarDataSets(ArrayList<BarEntry> yVals) {
        ArrayList<BarDataSet> dataSets = new ArrayList<BarDataSet>();
        BarDataSet observationDataSet = new BarDataSet(yVals, "Observationer");
        observationDataSet.setBarSpacePercent(35f);
        observationDataSet.setDrawValues(false);
        observationDataSet.setValueTextColor(Color.WHITE);
        dataSets.add(observationDataSet);
        return dataSets;
    }

    private ArrayList<BarEntry> getBarEntries(StatsType statsType, List<ObsStats> stats) {
        ArrayList<BarEntry> yVals = new ArrayList<BarEntry>();

        for (ObsStats obsStats : stats) {
            if (StatsType.STATS_MONTHLY.equals(statsType)) {
                yVals.add(new BarEntry(obsStats.getObservations(), obsStats.getMonth() - 1 ));
            } else {
                yVals.add(new BarEntry(obsStats.getObservations(), obsStats.getWeek() - 1 ));
            }
        }
        return yVals;
    }

    public class BarChartMarkerView extends MarkerView {

        private TextView markerText;
        private StatsType statsType;

        public BarChartMarkerView (Context context, int layoutResource, StatsType statsType) {
            super(context, layoutResource);
            this.statsType = statsType;
            markerText = (TextView) findViewById(R.id.barChartDescriptor);
        }

        // callbacks everytime the MarkerView is redrawn, can be used to update the
        // content (user-interface)
        @Override
        public void refreshContent(Entry e, Highlight highlight) {
            if (StatsType.STATS_WEEKLY.equals(statsType)) {
                markerText.setText(getFormattedDatePeriod(e.getXIndex()));
            }
        }

        @Override
        public int getXOffset() {
            // this will center the marker-view horizontally
            return -(getWidth() / 2);    }

        @Override
        public int getYOffset() {
            // this will cause the marker-view to be above the selected value
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


