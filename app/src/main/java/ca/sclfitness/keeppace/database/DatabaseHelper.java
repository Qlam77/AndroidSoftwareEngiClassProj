package ca.sclfitness.keeppace.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import ca.sclfitness.keeppace.model.Race;

/**
 * DatabaseHelper class creates SQLite connection.
 * Initializes database tables.
 *
 * @version kp 1.0
 * @author Jason, Tzu Hsiang Chen
 * @since November 12, 2017
 */

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String TAG = DatabaseHelper.class.getSimpleName();
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "kp.db";

    // Create helper instance
    private static DatabaseHelper instance;

    /**
     * Inherit SQLiteOpenHelper constructor.
     * Setup database name and version.
     *
     * @param context - current context.
     */
    private DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * Start database instance.
     *
     * @param context - current context.
     * @return databaseHelper - current instance.
     */
    public static synchronized DatabaseHelper getInstance(Context context) {
        if (instance == null) {
            instance = new DatabaseHelper(context);
        }
        return instance;
    }

    /**
     * Create SQLite database.
     *
     * @param db - SQLiteDatabase object.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.i(TAG, "Create SQLite database version " + DATABASE_VERSION);
        try {
            db.execSQL(IRace.CREATE_RACE_TABLE);
            db.execSQL(IRecord.CREATE_RECORD_TABLE);
            // insert races
            Race[] races = new RaceSeed().getRaces();
            for (Race race : races) {
                this.insertRaces(db, race);
            }

        } catch (SQLiteException e) {
            Log.e(TAG, "Cannot create table - " + e.getMessage());
        }

    }

    /**
     * Upgrade SQLite database.
     *
     * @param db - SQLiteDatabase object.
     * @param oldVersion - current version on the mobile.
     * @param newVersion - new version.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion != newVersion) {
            Log.i(TAG, "Upgrade SQLite database from version " + oldVersion + " to version " + newVersion);
            try {
                db.execSQL(IRace.DROP_RACE_TABLE);
                db.execSQL(IRecord.DROP_RECORD_TABLE);
                this.onCreate(db);
            } catch (SQLiteException e) {
                Log.e(TAG, "Cannot upgrade table - " + e.getMessage());
            }
        }
    }

    /**
     * Insert current race.
     *
     * @param db - SQLiteDatabase object
     * @param race - Race object
     */
    private void insertRaces(SQLiteDatabase db, Race race) {
        ContentValues values = new ContentValues();
        values.put(IRace.RACE_ID_COLUMN, race.getId());
        values.put(IRace.RACE_NAME_COLUMN, race.getName());
        values.put(IRace.RACE_DISTANCE_COLUMN, race.getDistance());
        values.put(IRace.RACE_MARKERS_COLUMN, race.getMarkers());

        long result = db.insert(IRace.RACE_TABLE_NAME, null, values);
        Log.d(TAG, "insert races " + result + " rows");
    }
}
