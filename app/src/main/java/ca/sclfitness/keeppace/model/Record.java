package ca.sclfitness.keeppace.model;

import java.util.Locale;

/**
 * @author Jason, Tzu Hsiang Chen
 * @since November 21, 2017
 */

public class Record {
    private int mId;
    private double mAveragePace;
    private long mTime;
    private int mRaceId;
    private String mDate;

    public Record(double averagePace, long time, String date, int raceId) {
        this.mAveragePace = averagePace;
        this.mTime = time;
        this.mDate = date;
        this.mRaceId = raceId;
    }

    public Record(int id, double averagePace, long time, String date, int raceId) {
        this.mId = id;
        this.mAveragePace = averagePace;
        this.mTime = time;
        this.mDate = date;
        this.mRaceId = raceId;
    }

    /**
     * Get record Id
     * @return record Id
     */
    public int getId() {
        return mId;
    }

    /**
     * Set record Id
     * @param id - record Id
     */
    public void setId(int id) {
        this.mId = id;
    }

    /**
     * Get average pace
     * @return average pace of the record
     */
    public double getAveragePace() {
        return mAveragePace;
    }

    /**
     * Set average pace
     * @param averagePace - average pace of the record
     */
    public void setAveragePace(double averagePace) {
        this.mAveragePace = averagePace;
    }

    /**
     * Get time hh:mm:ss and mm:ss.ss
     * @return record of the time
     */
    public long getTime() {
        return mTime;
    }

    /**
     * Set time hh:mm:ss and mm:ss.ss
     * @param time - time of the record
     */
    public void setTime(long time) {
        this.mTime = time;
    }

    /**
     * Get race Id
     * @return race Id
     */
    public int getRaceId() {
        return mRaceId;
    }

    /**
     * Set race Id
     *
     * @param raceId - race Id
     */
    public void setRaceId(int raceId) {
        this.mRaceId = raceId;
    }

    public String getDate() {
        return mDate;
    }

    public void setDate(String date) {
        this.mDate = date;
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
}
