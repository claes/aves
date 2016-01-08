/*
 * Copyright (c) Claes Holmerson 2013, 2015. The program is licensed under GNU GPL v3. See LICENSE.txt for details.
 */

package se.eliga.aves.model;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by Claes on 2013-07-19.
 */
public class DatabaseHandler extends SQLiteOpenHelper {

    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 9;

    // Database Name
    private static final String DATABASE_NAME = "birdsDb12";

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

    private final Context context;

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {

        try {

            {
                db.execSQL("create table ordersAndFamilies(" +
                        FAMILY_SORTORDER_KEY_COLUMN + " text, " +
                        LATIN_ORDER_COLUMN + " text, " +
                        ENGLISH_ORDER_COLUMN + " text, " +
                        SWEDISH_ORDER_COLUMN + " text, " +
                        LATIN_FAMILY_COLUMN + " text, " +
                        ENGLISH_FAMILY_COLUMN + " text, " +
                        SWEDISH_FAMILY_COLUMN + " text)");

                InputStream is = context.getAssets().open("data/ordersAndFamilies.txt");
                BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
                String line;
                while ((line = reader.readLine()) != null) {
                    String columns[] = line.split(";");
                    db.execSQL("insert into ordersAndFamilies values(?,?,?,?,?,?,?)", columns);
                }
                reader.close();

                db.execSQL("create index cafIdx on ordersAndFamilies(" + FAMILY_SORTORDER_KEY_COLUMN + ")");
            }

            {
                db.execSQL("create table speciesData(" +
                                SPECIES_SORTORDER_KEY_COLUMN + " text, " +
                                FAMILY_SORTORDER_FK_COLUMN + " text, " +
                                LATIN_SPECIES_COLUMN + " text, " +
                                ENGLISH_SPECIES_COLUMN + " text, " +
                                SWEDISH_SPECIES_COLUMN + " text, " +
                                DYNTAXA_TAXONID_COLUMN + " text, " +
                                SOF_STATUS_COLUMN + " text, " +
                                SWEDISH_REDLIST_CATEGORY_COLUMN + " text," +
                                POPULATION_MIN_ESTIMATE_COLUMN + " text," +
                                POPULATION_MAX_ESTIMATE_COLUMN + " text," +
                                POPULATION_BEST_ESTIMATE_COLUMN + " text," +
                                POPULATION_UNIT_COLUMN + " text," +
                                POPULATION_TYPE_COLUMN + " text)"
                );
                    InputStream is = context.getAssets().open("data/iocAndSofData.txt");
                    BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        String columns[] = line.split(";");
                        db.execSQL("insert into speciesData values(?,?,?,?,?,?,?,?,?,?,?,?,?)", columns);
                    }
                    reader.close();

                db.execSQL("create index sdIdx1 on speciesData(" + SPECIES_SORTORDER_KEY_COLUMN + ")");
                db.execSQL("create index sdIdx2 on speciesData(" + FAMILY_SORTORDER_FK_COLUMN + ")");
            }

            {
                db.execSQL("create table birdlifeData(" +
                        IOC_LATIN_SPECIES_COLUMN + " text, " +
                        BIRDLIFE_LATIN_SPECIES_COLUMN + " text, " +
                        BIRDLIFE_RECOGNIZED_SPECIES_COLUMN + " text, " +
                        GLOBAL_REDLIST_CATEGORY_COLUMN + " text, " +
                        SIS_RECID_COLUMN + " text, " +
                        SPC_RECID_COLUMN + " text)");

                InputStream is = context.getAssets().open("data/birdLifeData.txt");
                BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));

                String line;
                while ((line = reader.readLine()) != null) {
                    String columns[] = line.split(";");
                    db.execSQL("insert into birdlifeData values(?,?,?,?,?,?)", columns);
                }
                reader.close();
            }

            {
                db.execSQL("create table locationStats(" +
                        DYNTAXA_TAXONID_COLUMN + " text, " +
                        AREA_ID + " text, " +
                        LOCATION + " text, " +
                        LATITUDE + " number, " +
                        LONGITUDE + " number, " +
                        OBSERVATIONS + " integer)");

                InputStream is = context.getAssets().open("data/localityStats.txt");
                BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
                String line;
                while ((line = reader.readLine()) != null) {
                    String columns[] = line.split(";");
                    db.execSQL("insert into locationStats values(?,?,?,?,?,?)", columns);
                }
                reader.close();
                db.execSQL("create index dyntaxaLocIdx on locationStats(" + DYNTAXA_TAXONID_COLUMN + ")");
                db.execSQL("create index areaLocIdx on locationStats(" + AREA_ID + ")");
            }

            {
                db.execSQL("create table observationStats(" +
                        DYNTAXA_TAXONID_COLUMN + " text, " +
                        AREA_ID + " text, " +
                        PERIOD_VALUE + " integer, " +
                        PERIOD_TYPE + " text, " +
                        OBSERVATIONS + " integer, " +
                        OBSERVED_INDIVIDUALS + " integer)");
                {
                    InputStream is = context.getAssets().open("data/observationData.txt");
                    BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        String columns[] = line.split(";");
                        String remappedColumns[] = new String[] {columns[2], columns[3], columns[4], columns[5], columns[0], columns[1]};
                        db.execSQL("insert into observationStats values(?,?,?,?,?,?)", remappedColumns);
                    }
                    reader.close();
                }
                db.execSQL("create index dyntaxaObsIdx on observationStats(" + DYNTAXA_TAXONID_COLUMN + ")");
                db.execSQL("create index areaObsIdx on observationStats(" + AREA_ID + ")");
                db.execSQL("create index periodTypeIdx on observationStats(" + PERIOD_TYPE + ")");
            }

            {
                db.execSQL("create table lan(" +
                        LAN_ID + " text, " +
                        LAN_NAME + " text)");
                {
                    InputStream is = context.getAssets().open("data/lan.txt");
                    BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        String columns[] = line.split(";");
                        db.execSQL("insert into lan values(?,?)", columns);
                    }
                    reader.close();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("drop table if exists ordersAndFamilies;");
        db.execSQL("drop table if exists speciesData;");
        db.execSQL("drop table if exists birdlifeData;");
        db.execSQL("drop table if exists observationStats;");
        db.execSQL("drop table if exists locationStats;");
        db.execSQL("drop table if exists lan;");

        // Create tables again
        onCreate(db);
    }

    public String getSpcRecId(String iocLatinSpecies) {
        SQLiteDatabase db = this.getReadableDatabase();
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
        SQLiteDatabase db = this.getReadableDatabase();
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
        SQLiteDatabase db = this.getReadableDatabase();
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
        SQLiteDatabase db = this.getReadableDatabase();
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
                stats.add(locationStat);
            } while (cursor.moveToNext());
        }
        return stats;
    }

    public List<ObsStats> getObsStats(String taxonId, String areaId) {
        List<ObsStats> stats = new ArrayList<ObsStats>();
        SQLiteDatabase db = this.getReadableDatabase();
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
        SQLiteDatabase db = this.getReadableDatabase();
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


}
