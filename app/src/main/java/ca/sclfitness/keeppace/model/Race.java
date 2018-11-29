package ca.sclfitness.keeppace.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Basic model for a race
 *
 * @version kp 1.0
 * @author Jason, Tzu Hsiang Chen
 * @since November 12, 2017
 */

public class Race {
    public static final double MILE_CONVERSION = 0.6214;
    public static final int MAX_RECORD_SETS = 10;
    public static final String DEFAULT_UNIT = "km";
    private int mId;
    private String mName;
    private double mDistance;
    private int mMarkers;
    private double mAveragePace;
    private long mTime;
    private String mUnit;
    private List<Record> mRecords;

    /**
     * Default constructor
     */
    public Race() {
        this.mId = 0;
        this.mName = "";
        this.mDistance = 0.0;
        this.mMarkers = 0;
        this.mAveragePace = 0.00;
        this.mTime = 0;
        this.mUnit = DEFAULT_UNIT;
        mRecords = new ArrayList<>(MAX_RECORD_SETS);
    }

    /**
     * Construct a race
     * @param name - name of the race
     * @param distance - total distance of the race
     * @param markers - total markers of the race
     */
    public Race(String name, double distance, int markers) {
        this.mId = 0;
        this.mName = name;
        this.mDistance = distance;
        this.mMarkers = markers;
        this.mAveragePace = 0.00;
        this.mTime = 0;
        this.mUnit = DEFAULT_UNIT;
        mRecords = new ArrayList<>(MAX_RECORD_SETS);
    }

    /**
     * replace a record with new record
     * @param recordId - record Id
     * @param newRecord - a new record
     */
    public void replace(int recordId, Record newRecord) {
        for (Record record : mRecords) {
            if (record.getId() == recordId) {
                record.setTime(newRecord.getTime());
                record.setAveragePace(newRecord.getAveragePace());
            }
        }
    }

    /**
     * Get worst record
     * @return a worst record from a list
     */
    public Record getWorstRecord() {
        if (mRecords.size() == 0) {
            return null;
        }

        Record record = mRecords.get(0);
        for (int i = 1; i < mRecords.size(); i++) {
            if (mRecords.get(i).getTime() > record.getTime()) {
                record = mRecords.get(i);
            }
        }
        return record;
    }

    /**
     * Get the best time from records
     * @return the best time of the race
     */
    public Record getBestRecord() {
        if (mRecords.size() == 0) {
            return null;
        }
        Record record = mRecords.get(0);
        for (int i = 1; i < mRecords.size(); i++) {
            if (mRecords.get(i).getTime() < record.getTime()) {
                record = mRecords.get(i);
            }
        }

        mTime = record.getTime();
        return record;
    }

    /**
     * Add record to the list of records
     * @param record - a record object
     * @return true if the size of the list is less than equal to 10; otherwise, false
     */
    public boolean addRecord(Record record) {
        if (mRecords.size() >= MAX_RECORD_SETS) {
            return false;
        }
        mRecords.add(record);
        return true;
    }

    /**
     * Converts kilometers to miles
     *
     * @param kilometer - distance in kilometer
     * @return distance in mile
     */
    public double convertToMile(double kilometer) {
        return kilometer * MILE_CONVERSION;
    }

    /**
     * Converts Miles to kilometers
     *
     * @param miles distance in miles
     * @return distance in kilometers
     */
    public double convertToKm(double miles) {
        return miles / MILE_CONVERSION;
    }

    /**
     * Get current pace
     *
     * @param currentMarker - current marker
     * @param currentTime - current time
     * @return current pace
     */
    public double getCurrentPace(int currentMarker, long currentTime) {
        double currentDistance = (double) currentMarker;
        return currentDistance / (double) currentTime;
    }

    /**
     * Get estimated finish time
     * @param pace - current speed
     * @return estimated finish time
     */
    public long getEstimateTime(double pace) {
        // mile for half marathon
        if (mUnit.equalsIgnoreCase("mile") && mName.equalsIgnoreCase("Half Marathon")) {
            return (long) (13.1 / pace);
        }

        // mile for full marathon
        if (mUnit.equalsIgnoreCase("mile") && mName.equalsIgnoreCase("Full Marathon")) {
            return (long) (26.2 / pace);
        }

        return (long) (mDistance / pace);
    }

    /**
     * Get marker names.
     *
     * @param count - current marker
     * @return marker name
     */
    public String getMarkerName(int count) {
        if (count == getMarkers()) {
            return "FINISH";
        }

        if (count <= 0 || count > getMarkers()) {
            return "";
        }

        if (mUnit.equals("mile")) {
            if (mName.equals("Half Marathon") || mName.equals("Full Marathon")) {
                return String.valueOf(count) + "MI";
            }
        }

        return String.valueOf(count) + "K";
    }

    /**
     * Converts milliseconds to hh:mm:ss or mm:ss.ss
     *
     * @param ms - milliseconds
     * @return time in String format
     */
    public String timeTextFormat(long ms) {
        int msec = (int) (ms / 10) % 100;
        int sec = (int) (ms / 1000);
        int min = sec / 60;
        int hour = min / 60;
        sec = sec % 60;
        min = min % 60;
        if (hour > 0) {
            return String.format(Locale.getDefault(), "%02d:%02d:%02d", hour, min, sec);
        }
        return String.format(Locale.getDefault(), "%02d:%02d.%02d", min, sec, msec);
    }

    /**
     * Converts estimate time to String
     *
     * @param pace - current speed
     * @return String format hh:mm:ss or mm:ss.ss
     */
    public String estimateTimeText(double pace) {
        return timeTextFormat(getEstimateTime(pace));
    }

    /**
     * Converts best time to String
     * @return String format hh:mm:ss or mm:ss.ss
     */
    public String getTimeText() {
        return timeTextFormat(mTime);
    }

    /**
     * Get race ID.
     *
     * @return mId - race ID as int.
     */
    public int getId() {
        return mId;
    }

    /**
     * Set race ID.
     *
     * @param id - race ID.
     */
    public void setId(int id) {
        this.mId = id;
    }

    /**
     * Get name of the race.
     * \
     * @return mName - name of the race as String.
     */
    public String getName() {
        return mName;
    }

    /**
     * Set name of the race.
     *
     * @param name - name of the race.
     */
    public void setName(String name) {
        this.mName = name;
    }

    /**
     * Get total distance.
     *
     * @return mDistance - total distance as double.
     */
    public double getDistance() {
        return mDistance;
    }

    /**
     * Set total distance.
     *
     * @param distance - total distance.
     */
    public void setDistance(double distance) {
        this.mDistance = distance;
    }

    /**
     * Get total markers
     * @return mMarkers - total markers as int
     */
    public int getMarkers() {
        if (mUnit.equals("mile")) {
            if (mName.equals("Half Marathon")) {
                return 13;
            } else if (mName.equals("Full Marathon")) {
                return 26;
            }
        }
        // km
        return mMarkers;
    }

    /**
     * Set total markers
     * @param markers - total markers
     */
    public void setMarkers(int markers) {
        this.mMarkers = markers;
    }

    /**
     * Get average pace of the race
     * @return mAveragePace - average pace as double
     */
    public double getAveragePace() {
        return mAveragePace;
    }

    /**
     * Set average pace of the race
     * @param averagePace - average pace
     */
    public void setAveragePace(double averagePace) {
        this.mAveragePace = averagePace;
    }

    /**
     * Get best time of the race
     * @return mTime - best time of the race as String
     */
    public long getTime() {
        return mTime;
    }

    /**
     * Set best time of the race
     * @param time - best time
     */
    public void setTime(long time) {
        this.mTime = time;
    }

    /**
     * Get unit of the race
     * @return unit - unit of distance
     */
    public String getUnit() {
        return mUnit;
    }

    /**
     * Set unit of the race
     * @param unit - unit of the race
     */
    public void setUnit(String unit) {
        this.mUnit = unit;
    }

    /**
     * Get records of the race
     * @return - a list of records
     */
    public List<Record> getRecords() {
        return mRecords;
    }

    /**
     * Set a list of records
     * @param records - a list of records
     */
    public void setRecords(List<Record> records) {
        // Can only save up to 10 records per race
        if (records.size() > MAX_RECORD_SETS) {
            return;
        }
        this.mRecords = records;
    }
}
