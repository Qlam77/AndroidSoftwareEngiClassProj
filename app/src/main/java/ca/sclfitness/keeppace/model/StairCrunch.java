package ca.sclfitness.keeppace.model;

/**
 * @author Jason, Tzu Hsiang Chen
 * @since November 21, 2017
 */

public class StairCrunch extends Race {
    private static final double MARKER_1 = 0.1144;
    private static final double MARKER_2 = 0.2288;
    private static final double MARKER_3 = 0.3432;
    private static final double MARKER_4 = 0.4576;
    private static final double MARKER_5 = 0.5720;
    private static final double MARKER_6 = 0.6864;
    private static final double MARKER_7 = 0.8008;
    private static final double MARKER_8 = 0.9152;

    public static final String STAIR_CRUNCH = "437 Steps";

    @Override
    public double getCurrentPace(int currentMarker, long currentTime) {
        switch (currentMarker) {
            case 1:
                return this.getDistance() * MARKER_1 / (double) currentTime;
            case 2:
                return this.getDistance() * MARKER_2 / (double) currentTime;
            case 3:
                return this.getDistance() * MARKER_3 / (double) currentTime;
            case 4:
                return this.getDistance() * MARKER_4 / (double) currentTime;
            case 5:
                return this.getDistance() * MARKER_5 / (double) currentTime;
            case 6:
                return this.getDistance() * MARKER_6 / (double) currentTime;
            case 7:
                return this.getDistance() * MARKER_7 / (double) currentTime;
            case 8:
                return this.getDistance() * MARKER_8 / (double) currentTime;
            default:
                return this.getDistance() / (double) currentTime;
        }
    }

    @Override
    public String getMarkerName(int count) {
        switch (count) {
            case 1:
                return "50";
            case 2:
                return "100";
            case 3:
                return "150";
            case 4:
                return "200";
            case 5:
                return "250";
            case 6:
                return "300";
            case 7:
                return "350";
            case 8:
                return "400";
            default:
                return super.getMarkerName(count);
        }
    }
}
