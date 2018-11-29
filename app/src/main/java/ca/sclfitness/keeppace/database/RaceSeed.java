package ca.sclfitness.keeppace.database;

import ca.sclfitness.keeppace.model.Race;

/**
 * RaceSeed setups current race mode
 *
 * @version kp 1.0
 * @author Jason, Tzu Hsiang Chen
 * @since November 12, 2017
 */

public class RaceSeed {

    private Race[] mRaces;
    private static final int numRaces = 7;

    public RaceSeed() {
        mRaces = new Race[numRaces];
        mRaces[0] = new Race("5K", 5.0, 5);
        mRaces[1] = new Race("10K", 10.0, 10);
        mRaces[2] = new Race("Half Marathon", 21.1, 21);
        mRaces[3] = new Race("Full Marathon", 42.2, 42);
        mRaces[4] = new Race("Grouse Grind", 2.2, 4);
        mRaces[5] = new Race("437 Steps", 0.3, 9);
        mRaces[6] = new Race("457 Steps", 0.29, 10);

        for (int i = 0; i < mRaces.length; i++) {
            mRaces[i].setId(i+1);
        }
    }

    public Race[] getRaces() {
        return mRaces;
    }
}
