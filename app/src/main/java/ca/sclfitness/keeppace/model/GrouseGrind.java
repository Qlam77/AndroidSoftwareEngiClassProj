package ca.sclfitness.keeppace.model;

/**
 * @author Jason, Tzu Hsiang Chen
 * @since November 13, 2017
 */

public class GrouseGrind extends Race {
    private static final double MARKER_1 = 0.33;
    private static final double MARKER_2 = 0.54;
    private static final double MARKER_3 = 0.78;
    public static final String GROUSE_GRIND = "Grouse Grind";

    @Override
    public double getCurrentPace(int currentMarker, long currentTime) {
        switch (currentMarker) {
            case 1:
                return this.getDistance() * MARKER_1 / (double) currentTime;
            case 2:
                return this.getDistance() * MARKER_2 / (double) currentTime;
            case 3:
                return this.getDistance() * MARKER_3 / (double) currentTime;
            default:
                return this.getDistance() / (double) currentTime;
        }
    }

    @Override
    public String getMarkerName(int count) {
        switch (count) {
            case 1:
                return "1/4";
            case 2:
                return "1/2";
            case 3:
                return "3/4";
            default:
                return super.getMarkerName(count);
        }
    }
}
