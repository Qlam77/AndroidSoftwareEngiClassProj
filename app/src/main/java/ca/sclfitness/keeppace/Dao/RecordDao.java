package ca.sclfitness.keeppace.Dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import ca.sclfitness.keeppace.database.IRace;
import ca.sclfitness.keeppace.database.IRecord;
import ca.sclfitness.keeppace.model.Record;

/**
 * RecordDao is a data access object class which
 * saves user's best time and average pace into different races.
 * Searches user's best time and average pace of different races.
 *
 * @version kp 1.0
 * @author Jason, Tzu Hsiang Chen
 * @since November 12, 2017
 */

public class RecordDao extends Dao {
    private static final String TAG = RecordDao.class.getSimpleName();

    public RecordDao(Context context) {
        super(context, IRecord.RECORD_TABLE_NAME);
    }

    /**
     * Insert a new record
     *
     * @param record - race object
     */
    public void insert(Record record) {
        ContentValues values = new ContentValues();
        values.put(IRecord.RECORD_AVERAGE_PACE_COLUMN, record.getAveragePace());
        values.put(IRecord.RECORD_TIME_COLUMN, record.getTime());
        values.put(IRecord.RECORD_DATE_COLUMN, record.getDate());
        values.put(IRace.RACE_ID_COLUMN, record.getRaceId());
        Log.i(TAG, "Inserting a new record");
        super.insert(values);
    }

    /**
     * Update average pace and time.
     *
     * @param record - record object.
     */
    public void update(int recordId, Record newRecord) {
        ContentValues values = new ContentValues();
        values.put(IRecord.RECORD_AVERAGE_PACE_COLUMN, newRecord.getAveragePace());
        values.put(IRecord.RECORD_TIME_COLUMN, newRecord.getTime());
        values.put(IRecord.RECORD_DATE_COLUMN, newRecord.getDate());
        String[] args = {String.valueOf(recordId)};
        Log.i(TAG, "Updating a record");
        super.update(IRecord.RECORD_ID_COLUMN, args, values);
    }

    /**
     * Delete a record
     * @param recordId - record Id
     */
    public void delete(int recordId) {
        String[] args = {String.valueOf(recordId)};
        Log.i(TAG, "Deleting a record");
        super.delete(IRecord.RECORD_ID_COLUMN, args);
    }

    /**
     * Find records associated with the race
     *
     * @param raceId - race id
     * @return list of records
     */
    public List<Record> findRecordsByRaceId(int raceId) {
        List<Record> records = null;
        try {
            SQLiteDatabase sqLiteDatabase = dbHelper.getReadableDatabase();
            Cursor cursor = sqLiteDatabase.rawQuery("SELECT DISTINCT * FROM " + IRecord.RECORD_TABLE_NAME
                    + " WHERE " + IRace.RACE_ID_COLUMN + " = '" + raceId + "' ORDER BY " + IRecord.RECORD_TIME_COLUMN + ";", null);
            int count = cursor.getCount();
            Log.d(TAG, "Found races " + count + " row");
            if (count > 0 && cursor.moveToFirst()) {
                records = new ArrayList<>(count);
                do {
                    Record record = new Record(
                            cursor.getInt(0),
                            cursor.getDouble(1),
                            cursor.getLong(2),
                            cursor.getString(3),
                            cursor.getInt(4)
                    );
                    records.add(record);
                } while (cursor.moveToNext());
            }
            cursor.close();
            sqLiteDatabase.close();
        } catch (SQLiteException e) {
            Log.e(TAG, e.getMessage());
        }
        return records;
    }
}
