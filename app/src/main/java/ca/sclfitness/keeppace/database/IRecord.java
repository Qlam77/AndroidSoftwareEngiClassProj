package ca.sclfitness.keeppace.database;

/**
 * @author Jason, Tzu Hsiang Chen
 * @since November 19, 2017
 */

public interface IRecord {
    // record table name
    String RECORD_TABLE_NAME = "Record";

    // column names
    String RECORD_ID_COLUMN = "recordId";
    String RECORD_AVERAGE_PACE_COLUMN = "averagePace";
    String RECORD_TIME_COLUMN = "time";
    String RECORD_DATE_COLUMN = "date";

    // create record table
    String CREATE_RECORD_TABLE = "CREATE TABLE " + RECORD_TABLE_NAME + "("
            + RECORD_ID_COLUMN + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + RECORD_AVERAGE_PACE_COLUMN + " REAL, "
            + RECORD_TIME_COLUMN + " INTEGER, "
            + RECORD_DATE_COLUMN + " TEXT, "
            + IRace.RACE_ID_COLUMN + " INTEGER, "
            + "FOREIGN KEY(" + IRace.RACE_ID_COLUMN + ") REFERENCES "
            + IRace.RACE_ID_COLUMN + "(" + IRace.RACE_ID_COLUMN +"));";

    // drop record table
    String DROP_RECORD_TABLE = "DROP IF EXISTS " + RECORD_TABLE_NAME;
}
