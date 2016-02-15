/*
 * Copyright (c) Claes Holmerson 2013, 2015. The program is licensed under GNU GPL v3. See LICENSE.txt for details.
 */

package se.eliga.aves;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.IOException;
import java.util.Observable;
import java.util.Observer;

import se.eliga.aves.birdlist.BirdListFragment;
import se.eliga.aves.model.DatabaseHandler;

public class MainActivity extends FragmentActivity {

    private static String TAG = MainActivity.class.getName();

    private DrawerLayout drawerLayout;
    private ListView drawerList;
    private ActionBarDrawerToggle drawerToggle;
    static DownloadDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerList = (ListView) findViewById(R.id.left_drawer);

        // Set the adapter for the list view
        String[] drawerMenuItems = getResources().getStringArray(R.array.drawer_menu_list);
        drawerList.setAdapter(new ArrayAdapter<String>(this,
                R.layout.drawer_list_item, drawerMenuItems));
        // Set the list's click listener
        drawerList.setOnItemClickListener(new DrawerItemClickListener());

        // enable ActionBar app icon to behave as action to toggle nav drawer
        getActionBar().setDisplayShowTitleEnabled(false);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);


        // ActionBarDrawerToggle ties together the the proper interactions
        // between the sliding drawer and the action bar app icon
        drawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                drawerLayout,         /* DrawerLayout object */
                R.drawable.ic_drawer,  /* nav drawer image to replace 'Up' caret */
                R.string.drawer_open,  /* "open drawer" description for accessibility */
                R.string.drawer_close  /* "close drawer" description for accessibility */
        ) {
            public void onDrawerClosed(View view) {
                getActionBar().setTitle(R.string.app_name);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            public void onDrawerOpened(View drawerView) {
                getActionBar().setTitle(R.string.app_name);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };
        drawerLayout.setDrawerListener(drawerToggle);

        if (! ((BirdApp) getApplication()).getDbHandler(this).checkAndOpenDatabaseIfExists(this)) {
            progressDialog = createAndShowDownloadDialog();
        } else {
            selectItem(0); //Start BirdListFragment
        }
    }

    /* Called whenever we call invalidateOptionsMenu() */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // If the nav drawer is open, hide action items related to the content view

        boolean drawerOpen = drawerLayout.isDrawerOpen(drawerList);

        MenuItem sortOrder = menu.findItem(R.id.sort_order);
        MenuItem filteringOptions = menu.findItem(R.id.filtering_options);
        MenuItem speciesSearch = menu.findItem(R.id.species_search);
        if (sortOrder != null) {
            sortOrder.setVisible(!drawerOpen);
        }
        if (filteringOptions != null) {
            filteringOptions.setVisible(!drawerOpen);
        }
        if (speciesSearch != null) {
            speciesSearch.setVisible(!drawerOpen);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // The action bar home/up action should open or close the drawer.
        // ActionBarDrawerToggle will take care of this.
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /* The click listener for ListView in the navigation drawer */
    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectItem(position);
        }
    }


    private void selectItem(int position) {
        // update the main content by replacing fragments
        Fragment fragment;

        if (position == 0) {
            fragment = new BirdListFragment();
        } else if (position == 1) {
            fragment = new HelpFragment();
        } else {
            fragment = new AboutFragment();
        }

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();

        // update selected item and title, then close the drawer
        drawerList.setItemChecked(position, true);
        //setTitle(position);
        drawerLayout.closeDrawer(drawerList);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggls
        drawerToggle.onConfigurationChanged(newConfig);
    }


    public static class DbProgressTask extends AsyncTask<Void, Void, Void> implements Observer {

        private DatabaseHandler dbHandler;
        private MainActivity activity;

        public DbProgressTask(DatabaseHandler dbHandler, MainActivity activity) {
            this.dbHandler = dbHandler;
            this.activity = activity;
        }

        @Override
        public void update(Observable observable, Object o) {
            publishProgress();
        }

        @Override
        protected Void doInBackground(Void ... params) {
            dbHandler.setInitObserver(this);
            try {
                dbHandler.downloadAndInitializeDatabase(activity);
            } catch (IOException e) {
                Log.e(TAG, "Exception while downloading / initializing database", e);
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            progressDialog.setMessage(dbHandler.getInitStatus().getStep());
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            progressDialog.dismiss();
            activity.selectItem(0); //Start BirdListFragment
        }
    }


    private DownloadDialog createAndShowDownloadDialog() {
        FragmentManager fm = getSupportFragmentManager();
        DownloadDialog downloadDialog = new DownloadDialog();
        downloadDialog.show(fm, "downloadDialog");
        return downloadDialog;
    }

    public static class DownloadDialog extends DialogFragment {

        private TextView downloadStatus;
        private TextView downloadNoWifiStatus;
        private Button continueButton;
        private Button cancelButton;
        private MainActivity activity;

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

            getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);

            ConnectivityManager cm =
                    (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);

            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

            boolean isConnected = (activeNetwork != null) &&
                    (activeNetwork.isConnectedOrConnecting());
            boolean isWiFi = (activeNetwork != null) && (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI);

            View view = inflater.inflate(R.layout.download_dialog, container);
            downloadStatus = (TextView) view.findViewById(R.id.downloadStatus);
            downloadNoWifiStatus = (TextView) view.findViewById(R.id.downloadNoWifiStatus);

            continueButton = (Button) view.findViewById(R.id.downloadContinue);
            cancelButton = (Button) view.findViewById(R.id.downloadCancel);

            if (!isConnected) {
                continueButton.setVisibility(View.GONE);
                downloadNoWifiStatus.setVisibility(View.GONE);
                downloadStatus.setText(R.string.downloadNoNetworkDescription);
            } else if (isWiFi) {
                downloadNoWifiStatus.setVisibility(View.GONE);
            }

            continueButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    DatabaseHandler dbHandler = ((BirdApp) getActivity().getApplication()).getDbHandler(getActivity());
                    DbProgressTask dbProgressTask = new DbProgressTask(dbHandler, activity);
                    dbProgressTask.execute();
                }
            });

            cancelButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    activity.moveTaskToBack(true);
                    android.os.Process.killProcess(android.os.Process.myPid());
                    System.exit(1);
                }
            });

            return view;
        }

        public void onAttach(Activity activity) {
            super.onAttach(activity);
            this.activity = (MainActivity) activity;
        }
        public void setMessage(String message) {
            downloadStatus.setText(message);
        }

    }

 }