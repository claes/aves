/*
 * Copyright (c) Claes Holmerson 2013, 2015. The program is licensed under GNU GPL v3. See LICENSE.txt for details.
 */

package se.eliga.aves.model;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Observer;
import java.util.Set;

/**
 * Created by Claes on 2013-07-19.
 */
public class DatabaseHandler  {


    private static String TAG = DatabaseHandler.class.getName();


    public static final String FAMILY_SORTORDER_KEY_COLUMN = "familySortOrder_id";
    public static final String LATIN_ORDER_COLUMN = "latinOrder";
    public static final String ENGLISH_ORDER_COLUMN = "englishOrder";
    public static final String SWEDISH_ORDER_COLUMN = "swedishOrder";
    public static final String LATIN_FAMILY_COLUMN = "latinFamily";
    public static final String ENGLISH_FAMILY_COLUMN = "englishFamily";
    public static final String SWEDISH_FAMILY_COLUMN = "swedishFamily";

    public static final String SPECIES_SORTORDER_KEY_COLUMN = "speciesSortOrder_id";
    public static final String FAMILY_SORTORDER_FK_COLUMN = "familySortOrder_fk";
    public static final String LATIN_SPECIES_COLUMN = "latinSpecies";
    public static final String SWEDISH_SPECIES_COLUMN = "swedishSpecies";
    public static final String ENGLISH_SPECIES_COLUMN = "englishSpecies";
    public static final String DYNTAXA_TAXONID_COLUMN = "dyntaxaTaxonId";
    public static final String SOF_STATUS_COLUMN = "sofStatus";
    public static final String SWEDISH_REDLIST_CATEGORY_COLUMN = "swedishRedlistCategory";

    public static final String IOC_LATIN_SPECIES_COLUMN = "iocLatinSpecies";
    public static final String BIRDLIFE_LATIN_SPECIES_COLUMN = "birdlifeLatinSpecies";
    public static final String BIRDLIFE_RECOGNIZED_SPECIES_COLUMN = "birdlifeRecognizedSpecies";
    public static final String GLOBAL_REDLIST_CATEGORY_COLUMN = "globalRedlistCategory";
    public static final String SIS_RECID_COLUMN = "sisRecId";
    public static final String SPC_RECID_COLUMN = "spcRecId";
    private static final String POPULATION_MIN_ESTIMATE_COLUMN = "populationMinEstimate";
    private static final String POPULATION_MAX_ESTIMATE_COLUMN = "populationMaxEstimate";
    private static final String POPULATION_BEST_ESTIMATE_COLUMN = "populationBestEstimate";
    private static final String POPULATION_UNIT_COLUMN = "populationUnit";
    private static final String POPULATION_TYPE_COLUMN = "populationType";
    public static final String AREA_ID = "areaId";
    public static final String LOCATION = "location";
    public static final String LATITUDE = "latitude";
    public static final String LONGITUDE = "longitude";
    public static final String OBSERVATIONS = "observations";
    public static final String PERIOD_VALUE = "period"; //month value or week value
    public static final String PERIOD_TYPE = "periodType";
    public static final String OBSERVED_INDIVIDUALS = "observedIndividuals";
    public static final String LAN_NAME = "lanName";
    public static final String LAN_ID = "lanId";

    private static final String DATABASE_FILE = "aves.db";

    private SQLiteDatabase db;

    public boolean checkAndOpenDatabaseIfExists(Context context) {
        File databaseFile = context.getDatabasePath(DATABASE_FILE);
        boolean exists = databaseFile.exists();
        if (exists) {
            db = context.openOrCreateDatabase(databaseFile.getAbsolutePath(), Context.MODE_PRIVATE, null);
        }
        return exists;
    }

    public void downloadAndInitializeDatabase(Context context) throws IOException {

        File databaseFile = context.getDatabasePath(DATABASE_FILE);
        if (!databaseFile.exists()) {
            InputStream in = null;
            OutputStream out = null;
            try {
                initStatus = new InitStatus("Laddar ned databas", 10);
                notifyObserver();

                URL url = new URL("https://s3-eu-west-1.amazonaws.com/files.eliga.se/fagelappen/aves.db");
                URLConnection urlConnection = url.openConnection();
                in = new BufferedInputStream(urlConnection.getInputStream());
                databaseFile.getParentFile().mkdirs();
                databaseFile.createNewFile();
                out = new FileOutputStream(databaseFile);
                byte[] buffer = new byte[1024];
                int read = 0;
                int i = 0;
                while ((read = in.read(buffer)) != -1) {
                    out.write(buffer, 0, read);
                    initStatus = new InitStatus( (i++) + " kilobyte nedladdat", 20);
                    notifyObserver();
                }
                in.close();
                out.close();
            } catch (IOException e) {
                Log.e(TAG, "Exception while downloading database ", e);

                databaseFile.delete();
                if (in != null) {
                    in.close();
                }
                if (out != null) {
                    out.close();
                }
                initStatus = new InitStatus("Nedladdning misslyckades", 20);
                notifyObserver();
                return;
            }
            initStatus = new InitStatus("Databas nedladdad", 20);
            notifyObserver();
        }

        db = context.openOrCreateDatabase(databaseFile.getAbsolutePath(), Context.MODE_PRIVATE, null);
        initStatus = new InitStatus("Databas öppnad", 30);
        notifyObserver();
    }

    public String getSpcRecId(String iocLatinSpecies) {
        Cursor cursor;
        StringBuffer query = new StringBuffer("select * from speciesData inner join birdlifeData" +
                " on speciesData." + LATIN_SPECIES_COLUMN + " = birdlifeData." + IOC_LATIN_SPECIES_COLUMN +
                " where 1=1");
        query.append(" AND birdlifeData." + IOC_LATIN_SPECIES_COLUMN + " = '" + iocLatinSpecies + "' ");
        String queryString = query.toString();
        cursor = db.rawQuery(queryString, null);
        String spcRecId = null;
        if (cursor.moveToFirst()) {
            do {
                spcRecId = cursor.getString(cursor.getColumnIndex(SPC_RECID_COLUMN));
                String recognizedSpecies = cursor.getString(cursor.getColumnIndex(BIRDLIFE_RECOGNIZED_SPECIES_COLUMN));
                if ("R".equals(recognizedSpecies)) {
                    break;
                }
            } while (cursor.moveToNext());
        }
        return spcRecId;
    }

    public String getSisRecId(String iocLatinSpecies) {
        Cursor cursor;
        StringBuffer query = new StringBuffer("select * from speciesData inner join birdlifeData" +
                " on speciesData." + LATIN_SPECIES_COLUMN + " = birdlifeData." + IOC_LATIN_SPECIES_COLUMN +
                " where 1=1");
        query.append(" AND birdlifeData." + IOC_LATIN_SPECIES_COLUMN + " = '" + iocLatinSpecies + "' ");
        String queryString = query.toString();
        cursor = db.rawQuery(queryString, null);
        String sisRecId = null;
        if (cursor.moveToFirst()) {
            do {
                sisRecId = cursor.getString(cursor.getColumnIndex(SIS_RECID_COLUMN));
                String recognizedSpecies = cursor.getString(cursor.getColumnIndex(BIRDLIFE_RECOGNIZED_SPECIES_COLUMN));
                if ("R".equals(recognizedSpecies)) {
                    break;
                }
            } while (cursor.moveToNext());
        }
        return sisRecId;
    }

    public List<Bird> getAllSpecies(String filterString, String filterFamily, Set<Bird.SofStatus> validStatusSet) {
        List<Bird> birds = new ArrayList<Bird>();
        Cursor cursor;

        StringBuffer query = new StringBuffer("select * from ordersAndFamilies inner join speciesData" +
                " on ordersAndFamilies." + FAMILY_SORTORDER_KEY_COLUMN + " = speciesData." + FAMILY_SORTORDER_FK_COLUMN +
                " where 1=1");
        if (filterString != null) {
            filterString = filterString.replace("'", "''"); //SQL injection fix
            query.append(" AND (" +
                    SWEDISH_SPECIES_COLUMN + " like '%" + filterString + "%' OR "+
                    LATIN_SPECIES_COLUMN + " like '%" + filterString + "%' OR "+
                    ENGLISH_SPECIES_COLUMN + " like '%" + filterString + "%' )");
        }
        if (filterFamily != null) {
            query.append(" AND " + SWEDISH_FAMILY_COLUMN + " = '" + filterFamily + "' ");
        }
        if (validStatusSet != null && ! validStatusSet.isEmpty()) {
            query.append(" AND ifnull(" + SOF_STATUS_COLUMN + ", 'W') in (");
            boolean first = true;
            for (Bird.SofStatus status : validStatusSet) {
                if (! first) {
                    query.append(", ");
                }
                first = false;
                query.append("'" + status.getText() + "'");
            }
            query.append(") ");
        }
        query.append(" order by speciesData." + SPECIES_SORTORDER_KEY_COLUMN);
        String queryString = query.toString();
        cursor = db.rawQuery(queryString, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Bird bird = new Bird();
                bird.setPhylogeneticSortId(cursor.getInt(cursor.getColumnIndex(SPECIES_SORTORDER_KEY_COLUMN)));
                bird.setLatinOrder(cursor.getString(cursor.getColumnIndex(LATIN_ORDER_COLUMN)));
                bird.setLatinFamily(cursor.getString(cursor.getColumnIndex(LATIN_FAMILY_COLUMN)));
                bird.setLatinSpecies(cursor.getString(cursor.getColumnIndex(LATIN_SPECIES_COLUMN)));
                bird.setSwedishOrder(cursor.getString(cursor.getColumnIndex(SWEDISH_ORDER_COLUMN)));
                bird.setSwedishFamily(cursor.getString(cursor.getColumnIndex(SWEDISH_FAMILY_COLUMN)));
                bird.setSwedishSpecies(cursor.getString(cursor.getColumnIndex(SWEDISH_SPECIES_COLUMN)));
                bird.setEnglishSpecies(cursor.getString(cursor.getColumnIndex(ENGLISH_SPECIES_COLUMN)));
                bird.setEnglishOrder(cursor.getString(cursor.getColumnIndex(ENGLISH_ORDER_COLUMN)));
                bird.setEnglishFamily(cursor.getString(cursor.getColumnIndex(ENGLISH_FAMILY_COLUMN)));
                bird.setDyntaxaTaxonId(cursor.getString(cursor.getColumnIndex(DYNTAXA_TAXONID_COLUMN)));
                bird.setMinPopulationEstimate(cursor.getInt(cursor.getColumnIndex(POPULATION_MIN_ESTIMATE_COLUMN)));
                bird.setMaxPopulationEstimate(cursor.getInt(cursor.getColumnIndex(POPULATION_MAX_ESTIMATE_COLUMN)));
                bird.setBestPopulationEstimate(cursor.isNull(cursor.getColumnIndex(POPULATION_BEST_ESTIMATE_COLUMN)) ?
                        -1 : cursor.getInt(cursor.getColumnIndex(POPULATION_BEST_ESTIMATE_COLUMN)));
                bird.setPopulationType(cursor.getString(cursor.getColumnIndex(POPULATION_TYPE_COLUMN)));
                bird.setPopulationUnit(Bird.PopulationUnit.fromString(cursor.getString(cursor.getColumnIndex(POPULATION_UNIT_COLUMN))));
                bird.setSofStatus(Bird.SofStatus.fromString(cursor.getString(cursor.getColumnIndex(SOF_STATUS_COLUMN))));
                bird.setSwedishRedlistCategory(Bird.RedlistCategory.fromString(cursor.getString(cursor.getColumnIndex(SWEDISH_REDLIST_CATEGORY_COLUMN))));
                if (bird.getSwedishSpecies() != null) {
                    birds.add(bird);
                }
            } while (cursor.moveToNext());
        }
        return birds;
    }

    public List<LocationStats> getLocationStats(String taxonId, String areaId) {

        List<LocationStats> stats = new ArrayList<LocationStats>();
        StringBuffer query = new StringBuffer("select * from locationStats where 1=1");
        query.append(" AND " + DYNTAXA_TAXONID_COLUMN + " = '" + taxonId + "' ");
        query.append(" AND " + AREA_ID + " = '" + areaId + "' ");
        query.append(" ORDER BY " + OBSERVATIONS + " DESC");
        String queryString = query.toString();
        Cursor cursor = db.rawQuery(queryString, null);

        if (cursor.moveToFirst()) {
            do {
                LocationStats locationStat = new LocationStats();
                locationStat.setDyntaxaTaxonId(taxonId);
                locationStat.setAreaId(areaId);
                locationStat.setLocality(cursor.getString(cursor.getColumnIndex(LOCATION)));
                locationStat.setCount(cursor.getInt(cursor.getColumnIndex(OBSERVATIONS)));
                locationStat.setLatitude(cursor.getString(cursor.getColumnIndex(LATITUDE)));
                locationStat.setLongitude(cursor.getString(cursor.getColumnIndex(LONGITUDE)));
                stats.add(locationStat);
            } while (cursor.moveToNext());
        }
        return stats;
    }

    public List<ObsStats> getObsStats(String taxonId, String areaId) {
        List<ObsStats> stats = new ArrayList<ObsStats>();
        StringBuffer query = new StringBuffer("select * from observationStats where 1=1");
        query.append(" AND " + DYNTAXA_TAXONID_COLUMN + " = '" + taxonId + "' ");
        query.append(" AND " + AREA_ID + " = '" + areaId + "' ");
        query.append(" AND " + PERIOD_TYPE + " = '" + "W" + "' ");
        query.append(" ORDER BY " + PERIOD_VALUE + " ASC");
        String queryString = query.toString();
        Cursor cursor = db.rawQuery(queryString, null);

        if (cursor.moveToFirst()) {
            do {
                ObsStats obsStats = new ObsStats();
                obsStats.setDyntaxaTaxonId(taxonId);
                obsStats.setAreaId(areaId);
                obsStats.setWeek(cursor.getInt(cursor.getColumnIndex(PERIOD_VALUE)));
                obsStats.setObservations(cursor.getInt(cursor.getColumnIndex(OBSERVATIONS)));
                obsStats.setObservedIndividuals(cursor.getInt(cursor.getColumnIndex(OBSERVED_INDIVIDUALS)));
                stats.add(obsStats);
            } while (cursor.moveToNext());
        }

        return stats;
    }

    public List<County> getCounties() {
        List<County> counties = new ArrayList<County>();
        StringBuffer query = new StringBuffer("select * from lan order by " + LAN_NAME);
        String queryString = query.toString();
        Cursor cursor = db.rawQuery(queryString, null);

        if (cursor.moveToFirst()) {
            do {
                County county = new County();
                county.setId(cursor.getString(cursor.getColumnIndex(LAN_ID)));
                county.setName(cursor.getString(cursor.getColumnIndex(LAN_NAME)));
                counties.add(county);
            } while (cursor.moveToNext());
        }
        return counties;
    }


    private InitStatus initStatus = new InitStatus("Oinitierad", 0);
    private boolean initialized = false;
    private Observer initObserver;

    public InitStatus getInitStatus() {
        return initStatus;
    }

    public boolean isInitialized() {
        return initialized;
    }

    public class InitStatus {

        private String step;
        private int progress;


        public InitStatus(String step, int progress) {
            this.step = step;
            this.progress = progress;
        }

        public String getStep() {
            return step;
        }

        public int getProgress() {
            return progress;
        }
    }

    public void notifyObserver() {
        if (initObserver != null) {
            initObserver.update(null, null);
        }
    }

    public void setInitObserver(Observer initObserver) {
        this.initObserver = initObserver;
    }

}
