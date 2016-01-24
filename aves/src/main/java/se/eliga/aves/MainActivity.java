/*
 * Copyright (c) Claes Holmerson 2013, 2015. The program is licensed under GNU GPL v3. See LICENSE.txt for details.
 */

package se.eliga.aves;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

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
    static ProgressDialog progressDialog;

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

        checkProceed(this);

        progressDialog = ProgressDialog.show(this, "Initierar data. Var god vänta.",
                "", true);

        DatabaseHandler dbHandler = ((BirdApp) getApplication()).getDbHandler(this);
        DbProgressTask dbProgressTask = new DbProgressTask(dbHandler);
        dbProgressTask.execute();
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
    protected void onStart() {
        super.onStart();
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


    class DbProgressTask extends AsyncTask<Void, Void, Void> implements Observer {


        private DatabaseHandler dbHandler;

        DbProgressTask(DatabaseHandler dbHandler) {
            this.dbHandler = dbHandler;
        }
        @Override
        public void update(Observable observable, Object o) {
            publishProgress();
        }


        @Override
        protected Void doInBackground(Void ... params) {
            dbHandler.setInitObserver(this);
            try {
                dbHandler.downloadAndInitializeDatabase(MainActivity.this);
            } catch (IOException e) {
                Log.e(TAG, "Exception while downloading / initializing database", e);
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            progressDialog.setMessage(dbHandler.getInitStatus().getStep());
            progressDialog.setProgress(dbHandler.getInitStatus().getProgress());
        }


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            progressDialog.dismiss();
            selectItem(0); //Start BirdListFragment
        }
    }

    private boolean checkProceed(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
        boolean isWiFi = activeNetwork.getType() == ConnectivityManager.TYPE_WIFI;
        return isConnected && isWiFi;
    }

 }