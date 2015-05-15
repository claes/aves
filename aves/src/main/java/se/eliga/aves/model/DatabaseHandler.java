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

/**
 * Created by Claes on 2013-07-19.
 */
public class DatabaseHandler extends SQLiteOpenHelper {

    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "birdsDb12";
    public static final String PHYLOGENIC_SORT_COLUMN = "_id";
    public static final String LATIN_ORDER_COLUMN = "latinOrder";
    public static final String LATIN_FAMILY_COLUMN = "latinFamily";
    public static final String LATIN_SPECIES_COLUMN = "latinSpecies";
    public static final String SWEDISH_ORDER_COLUMN = "swedishOrder";
    public static final String SWEDISH_FAMILY_COLUMN = "swedishFamily";
    public static final String SWEDISH_SPECIES_COLUMN = "swedishSpecies";
    public static final String ENGLISH_SPECIES_COLUMN = "englishSpecies";
    public static final String STATUS_COLUMN = "status";

    private final Context context;

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {

        try {
            db.execSQL("create table species(" +
            		PHYLOGENIC_SORT_COLUMN + " text, " +
            		LATIN_ORDER_COLUMN + " text, " +
            		LATIN_FAMILY_COLUMN + " text, " +
            		LATIN_SPECIES_COLUMN + " text, " +
            		SWEDISH_ORDER_COLUMN + " text, " +
            		SWEDISH_FAMILY_COLUMN + " text, " +
            		SWEDISH_SPECIES_COLUMN + " text, " +
            		ENGLISH_SPECIES_COLUMN + " text, " +
            		STATUS_COLUMN + " text)");

            InputStream is = context.getAssets().open("birdspecies.txt");
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            String line;
            while ((line = reader.readLine()) != null) {
                String columns[] = line.split(";");
                db.execSQL("insert into species values (?,?,?,?,?,?,?,?,?)", columns);
            }
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("drop table if exists species");

        // Create tables again
        onCreate(db);
    }

    public List<Bird> getAllSpecies(String filterString) {
        List<Bird> birds = new ArrayList<Bird>();
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor;
        if (filterString == null) {
            cursor = db.rawQuery("select * from species order by " + PHYLOGENIC_SORT_COLUMN, null);
        } else {
            cursor = db.rawQuery("select * from species where " + SWEDISH_SPECIES_COLUMN +
                    " like '%" + filterString + "%'order by " + PHYLOGENIC_SORT_COLUMN, null);
        }

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {

                Bird bird = new Bird();
                bird.setPhylogeneticSortId(cursor.getInt(cursor.getColumnIndex(PHYLOGENIC_SORT_COLUMN)));
                bird.setLatinOrder(cursor.getString(cursor.getColumnIndex(LATIN_ORDER_COLUMN)));
                bird.setLatinFamily(cursor.getString(cursor.getColumnIndex(LATIN_FAMILY_COLUMN)));
                bird.setLatinSpecies(cursor.getString(cursor.getColumnIndex(LATIN_SPECIES_COLUMN)));
                bird.setSwedishOrder(cursor.getString(cursor.getColumnIndex(SWEDISH_ORDER_COLUMN))); 
                bird.setSwedishFamily(cursor.getString(cursor.getColumnIndex(SWEDISH_FAMILY_COLUMN))); 
                bird.setSwedishSpecies(cursor.getString(cursor.getColumnIndex(SWEDISH_SPECIES_COLUMN))); 
                bird.setEnglishSpecies(cursor.getString(cursor.getColumnIndex(ENGLISH_SPECIES_COLUMN)));
                bird.setStatus(Bird.Status.fromString(cursor.getString(cursor.getColumnIndex(STATUS_COLUMN))));                
                if (bird.getSwedishSpecies() != null) {
                    birds.add(bird);
                }
            } while (cursor.moveToNext());
        }
        return birds;
    }

    

//  private static String DB_PATH = "/data/data/" + "se.eliga.aves" + "/databases/";
//  private SQLiteDatabase database;
//  private final Context context;
//
//  /**
//   * Creates a empty database on the system and rewrites it with your own database.
//   */
//  public void createDataBase() throws IOException {
//
//      boolean dbExist = checkDataBase();
//      if (dbExist) {
//          //do nothing - database already exist
//      } else {
//          //By calling this method and empty database will be created into the default system path
//          //of your application so we are gonna be able to overwrite that database with our database.
//          this.getReadableDatabase();
//          try {
//              copyDataBase();
//          } catch (IOException e) {
//              throw new Error("Error copying database");
//          }
//      }
//  }
//
//
//  /**
//   * Check if the database already exist to avoid re-copying the file each time you open the application.
//   *
//   * @return true if it exists, false if it doesn't
//   */
//  private boolean checkDataBase() {
//
//      SQLiteDatabase checkDB = null;
//      try {
//          String myPath = DB_PATH + DATABASE_NAME;
//          checkDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
//      } catch (SQLiteException e) {
//          //database doesn't exist yet.
//      }
//
//      if (checkDB != null) {
//          checkDB.close();
//      }
//
//      return checkDB != null ? true : false;
//  }
//
//  /**
//   * Copies your database from your local assets-folder to the just created empty database in the
//   * system folder, from where it can be accessed and handled.
//   * This is done by transfering bytestream.
//   */
//  private void copyDataBase() throws IOException {
//
//      //Open your local db as the input stream
//      InputStream is = context.getAssets().open(DATABASE_NAME);
//
//      // Path to the just created empty db
//      String outFileName = DB_PATH + DATABASE_NAME;
//
//      //Open the empty db as the output stream
//      OutputStream os = new FileOutputStream(outFileName);
//
//      //transfer bytes from the inputfile to the outputfile
//      byte[] buffer = new byte[1024];
//      int length;
//      while ((length = is.read(buffer)) > 0) {
//          os.write(buffer, 0, length);
//      }
//
//      //Close the streams
//      os.flush();
//      os.close();
//      is.close();
//  }
//
//  public void openDataBase() {
//      //Open the database
//      String myPath = DB_PATH + DATABASE_NAME;
//      database = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
//  }
//
//  @Override
//  public synchronized void close() {
//
//      if (database != null)
//          database.close();
//
//      super.close();
//  }
//
//  @Override
//  public void onCreate(SQLiteDatabase db) {
//
//  }
//
//  @Override
//  public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
//
//  }
    
}
