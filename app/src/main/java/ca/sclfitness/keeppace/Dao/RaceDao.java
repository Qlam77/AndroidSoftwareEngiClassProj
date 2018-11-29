package ca.sclfitness.keeppace.Dao;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import ca.sclfitness.keeppace.database.IRace;
import ca.sclfitness.keeppace.model.FullCrunch;
import ca.sclfitness.keeppace.model.GrouseGrind;
import ca.sclfitness.keeppace.model.Race;
import ca.sclfitness.keeppace.model.Record;
import ca.sclfitness.keeppace.model.StairCrunch;

/**
 * @author Jason, Tzu Hsiang Chen
 * @since November 19, 2017
 */

public class RaceDao extends Dao {

    private static final String TAG = RaceDao.class.getSimpleName();
    private Context mContext;

    public RaceDao(Context context) {
        super(context, IRace.RACE_TABLE_NAME);
        this.mContext = context;
    }

    /**
     * Find a race by name.
     *
     * @param name - name of the race.
     * @return race - a race object if it is found.
     */
    public Race findRaceByName(String name) {
        Race race = null;
        try {
            SQLiteDatabase db = dbHelper.getReadableDatabase();
            Cursor cursor = db.rawQuery("SELECT DISTINCT * FROM " + IRace.RACE_TABLE_NAME
                    + " WHERE " + IRace.RACE_NAME_COLUMN + " = '" + name + "';", null);

            Log.d(TAG, "Found race " + cursor.getCount() + " row");
            if (cursor.moveToFirst()) {
                if (name.equalsIgnoreCase(GrouseGrind.GROUSE_GRIND)) {
                    Log.d(TAG, "Grouse Grind initialized");
                    race = new GrouseGrind();
                } else if (name.equalsIgnoreCase(FullCrunch.FULL_CRUNCH)) {
                    Log.d(TAG, "Full Crunch initialized");
                    race = new FullCrunch();
                } else if (name.equalsIgnoreCase(StairCrunch.STAIR_CRUNCH)) {
                    Log.d(TAG, "Stair Crunch initialized");
                    race = new StairCrunch();
                } else {
                    Log.d(TAG, "Basic race initialized");
                    race = new Race();
                }
                race.setId(cursor.getInt(0));
                race.setName(cursor.getString(1));
                race.setDistance(cursor.getDouble(2));
                race.setMarkers(cursor.getInt(3));
                RecordDao recordDao = new RecordDao(mContext);
                List<Record> records = recordDao.findRecordsByRaceId(cursor.getInt(0));
                if (records != null) {
                    race.setRecords(records);
                }
                recordDao.close();
            }
            cursor.close();
            db.close();
        } catch (SQLiteException e) {
            Log.e(TAG, e.getMessage());
        }

        return race;
    }

    /**
     * Find all the races.
     *
     * @return races - a list of races if they are found.
     */
    public List<Race> findAllRaces() {
        List<Race> races = null;
        try {
            SQLiteDatabase sqLiteDatabase = dbHelper.getReadableDatabase();
            Cursor cursor = sqLiteDatabase.rawQuery("SELECT DISTINCT * FROM " + IRace.RACE_TABLE_NAME, null);
            int count = cursor.getCount();
            Log.d(TAG, "Found races " + count + " row");
            if (count > 0 && cursor.moveToFirst()) {
                races = new ArrayList<>(count);
                do {
                    Race race = new Race();
                    race.setId(cursor.getInt(0));
                    race.setName(cursor.getString(1));
                    race.setDistance(cursor.getDouble(2));
                    race.setMarkers(cursor.getInt(3));
                    RecordDao recordDao = new RecordDao(mContext);
                    List<Record> records = recordDao.findRecordsByRaceId(cursor.getInt(0));
                    if (records != null) {
                        race.setRecords(records);
                    }
                    recordDao.close();
                    races.add(race);
                } while (cursor.moveToNext());
            }
            cursor.close();
            sqLiteDatabase.close();
        } catch (SQLiteException e) {
            Log.e(TAG, e.getMessage());
        }
        return races;
    }

    /**
     * Get the race along with records
     * @param raceId
     * @return
     */
    public Race getRaceByIdWithRecords(int raceId) {
        Race race = null;
        try {
            SQLiteDatabase db = dbHelper.getReadableDatabase();
            Cursor cursor = db.rawQuery("SELECT DISTINCT * FROM " + IRace.RACE_TABLE_NAME
                    + " WHERE " + IRace.RACE_ID_COLUMN + " = '" + raceId + "';", null);

            Log.d(TAG, "Found race " + cursor.getCount() + " row");
            if (cursor.getCount() == 1 && cursor.moveToFirst()) {
                race.setId(cursor.getInt(0));
                race.setName(cursor.getString(1));
                race.setDistance(cursor.getDouble(2));
                race.setMarkers(cursor.getInt(3));
                RecordDao recordDao = new RecordDao(mContext);
                List<Record> records = recordDao.findRecordsByRaceId(raceId);
                race.setRecords(records);
                recordDao.close();
            }
            cursor.close();
            db.close();
        } catch (SQLiteException e) {
            Log.e(TAG, e.getMessage());
        }

        return race;
    }
}
