package ca.sclfitness.keeppace;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.w3c.dom.Text;

public class PaceActivity extends AppCompatActivity {

    private int paceType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        PreferenceHelper p = new PreferenceHelper(this);
        p.setTheme();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pace);

        if (getSupportActionBar() != null)
        {
            getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
            getSupportActionBar().setCustomView(R.layout.intermediate_menu);
        }

        final Button raceTypeBtn = (Button) findViewById(R.id.btn_pace_paceType);
        final TextView titleBarTv = (TextView) findViewById(R.id.textView_transition_title);
        Intent i = getIntent();
        paceType = i.getIntExtra("type", -1);

        switch (paceType) {
            case 0:
                // Grind Pace
                raceTypeBtn.setText(R.string.pace_grind);
                titleBarTv.setText(R.string.title_grind);
                break;
            case 1:
                // 437 Steps
                raceTypeBtn.setText(getString(R.string.pace_steps));
                titleBarTv.setText(R.string.main_stairCrunch);
                break;
            case 2:
                // 457 Steps
                raceTypeBtn.setText(getString(R.string.pace_crunch));
                titleBarTv.setText(R.string.main_fullCrunch);
                break;
            case 3:
                // 5K Race
                raceTypeBtn.setText(R.string.pace_race);
                titleBarTv.setText(R.string.race_fiveK);
                break;
            case 4:
                // 10K Race
                raceTypeBtn.setText(R.string.pace_race);
                titleBarTv.setText(R.string.race_tenK);
                break;
            case 5:
                // Half Marathon
                raceTypeBtn.setText(R.string.pace_race);
                titleBarTv.setText(R.string.race_halfMarathon);
                break;
            case 6:
                // Full Marathon
                raceTypeBtn.setText(R.string.pace_race);
                titleBarTv.setText(R.string.race_fullMarathon);
                break;
            default:
                System.err.println("passing error code " + paceType);
                finish();
        }

    }

    public void onJustClick(View v) {
        beatTimeIntent(false);
    }

    public void onBeatClick(View v) {
        beatTimeIntent(true);
    }

    /**
     * pass information to next intent
     * depend on which arguments is passed, it will show different intent
     *
     * @param beatTime - true if user select beat your best time
     */
    private void beatTimeIntent(boolean beatTime) {
        Intent intent = null;
        if (paceType == 0) {
            startTimerIntent("Grouse Grind", beatTime);
        } else if (paceType == 1) {
            startTimerIntent("437 Steps", beatTime);
        } else if (paceType == 2) {
            startTimerIntent("457 Steps", beatTime);
        } else if (paceType == 3) {
            startTimerIntent("5K", beatTime);
        } else if (paceType == 4) {
            startTimerIntent("10K", beatTime);
        } else if (paceType == 5) {
            startTimerIntent("Half Marathon", beatTime);
        } else if (paceType == 6) {
            startTimerIntent("Full Marathon", beatTime);
        } else {
            System.err.println("passing error code " + paceType);
            finish();
        }
    }

    /**
     * Start timer activity and pass arguments for creating a race
     *
     * @param raceType - name of the race
     */
    private void startTimerIntent(String raceType, boolean beatBestTime) {
        Intent timerIntent = new Intent(this, TimerActivity.class);
        timerIntent.putExtra("raceType", raceType);
        timerIntent.putExtra("beatTime", beatBestTime);
        startActivity(timerIntent);
    }
}
