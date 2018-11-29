package ca.sclfitness.keeppace;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import ca.sclfitness.keeppace.Dao.RaceDao;
import ca.sclfitness.keeppace.Dao.RecordDao;
import ca.sclfitness.keeppace.model.FullCrunch;
import ca.sclfitness.keeppace.model.Race;
import ca.sclfitness.keeppace.model.Record;

import static android.view.View.GONE;

public class TimerActivity extends AppCompatActivity {

    private static final String TAG = TimerActivity.class.getSimpleName();

    // Pause check
    private boolean isPaused = false;

    // Finish Check
    private boolean isFinished = false;

    // Beat your best mode toggle
    private boolean beatTime = false;

    // race views
    private TextView currentTimeView, estimatedTimeView, currentSpeedView, beatTimeLabel, beatTimeView;
    // race
    private Race race;
    // timer
    private Handler handler;
    private long millisecondTime, startTime, timeBuff, updateTime = 0L;
    // buttons
    private Button pauseResumeBtn, saveBtn, startBtn, resetBtn;

    // markers scroll view
    private HorizontalScrollView scrollView;

    // markers margin
    private final int BTN_MARGIN = 100;

    // button marker size
    private int btn_size;

    // raceId of current view
    private int raceId;

    // array of the start drawable locations
    private int[] startBackgroundArray = {0, 0, 0, 0, R.drawable.grindstartblue, R.drawable.start437blue, R.drawable.start457blue};

    // array of the finish drawable locations
    private int[] finishBackgroundArray = {0, 0, 0, 0, R.drawable.grindfinishblue, R.drawable.finish437blue, R.drawable.finish457blue};


    // Timer thread
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            millisecondTime = SystemClock.uptimeMillis() - startTime;
            updateTime = timeBuff + millisecondTime;
            currentTimeView.setText(race.timeTextFormat(updateTime));
            //System.out.println(currentTimeView.getText());
            handler.postDelayed(this,50);
        }
    };

    /**
     * Create timer activity
     *
     * @param savedInstanceState - save current state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        PreferenceHelper p = new PreferenceHelper(this);
        p.setTheme();
        super.onCreate(savedInstanceState);
        if(getIntent().getBooleanExtra("beatTime", false) == false) {
            setContentView(R.layout.activity_timer);
        } else {
            setContentView(R.layout.activity_best_timer);
        }

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        btn_size = (int)(height * 0.2);

        // views
        currentTimeView = (TextView) findViewById(R.id.textView_timer_currentTime);
        estimatedTimeView = (TextView) findViewById(R.id.textView_timer_estimatedTime);
        currentSpeedView = (TextView) findViewById(R.id.textView_timer_pace);
        beatTimeLabel = (TextView) findViewById(R.id.textView_timer_beatTimeLabel);
        beatTimeView = (TextView) findViewById(R.id.textView_timer_beatTime);
        pauseResumeBtn = (Button) findViewById(R.id.button_timer_pauseResume);
        resetBtn = (Button) findViewById(R.id.button_timer_reset);
        startBtn = (Button) findViewById(R.id.button_timer_start);
        saveBtn = (Button) findViewById(R.id.button_timer_save);
        scrollView = (HorizontalScrollView) findViewById(R.id.scrollView_timer_markers);

        Intent intent = getIntent();
        beatTime = intent.getBooleanExtra("beatTime", false);
        String raceName = intent.getStringExtra("raceType");

        // setup race
        this.raceSetup(raceName);
        raceId = race.getId() - 1;
        System.out.println(race.getName());

//        if (getSupportActionBar() != null) {
//            getSupportActionBar().setTitle(getResources().getString(R.string.app_title) + " - " + raceName);
//        }

        // Get unit preference
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String unit = sharedPreferences.getString(getString(R.string.key_unit), "1");
        if (unit.equals("2")) {
            currentSpeedView.append(" " + getString(R.string.pace_mile_per_hr));
            race.setUnit("mile");
        } else {
            currentSpeedView.append(" " + getString(R.string.pace_km_per_hr));
        }

        // make start marker, based on race
        this.makeStartMarker();

        // make markers scroll view based on the race
        if (race.getName().equals(FullCrunch.FULL_CRUNCH)) {
            this.makeCrunchMarkers();
        } else {
            this.makeMarkers();
        }

        if (beatTime) {
            beatTimeLabel.setVisibility(View.VISIBLE);
            beatTimeView.setVisibility(View.VISIBLE);
            Record record = race.getBestRecord();
            if (record != null) {
                beatTimeView.setText(race.timeTextFormat(record.getTime()));
            }
        }

        // timer handler
        handler = new Handler();
    }

    public void onDestroy()
    {
        super.onDestroy();
        handler.removeCallbacks(runnable);
    }

    /**
     * Setup race object and initialize it
     *
     * @param raceName - name of the race
     */
    private void raceSetup(String raceName) {
        RaceDao raceDao = new RaceDao(TimerActivity.this);
        race = raceDao.findRaceByName(raceName);
        raceDao.close();
        if (race != null) {
            Log.d(TAG, "Found " + raceName);
            RecordDao recordDao = new RecordDao(TimerActivity.this);
            List<Record> records = recordDao.findRecordsByRaceId(race.getId());
            if (records != null) {
                Log.d(TAG, records.size() + " records found");
                race.setRecords(records);
            } else {
                Log.d(TAG, "No Record is found");
            }
            recordDao.close();
        } else {
            Log.d(TAG, raceName + " not found");
        }
    }

    /**
     * Start the timer if the race is not finished
     */
    private void startTimer() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        if (!isFinished) {
            startTime = SystemClock.uptimeMillis();
            handler.postDelayed(runnable, 0);
            startBtn.setVisibility(View.INVISIBLE);
            isPaused = false;

            String mode = sharedPreferences.getString(getString(R.string.key_mode), "1");
            if (mode.equals("2")) {
                pauseResumeBtn.setVisibility(GONE);
                pauseResumeBtn.setEnabled(false);
                resetBtn.setEnabled(true);
                resetBtn.setVisibility(View.VISIBLE);
            } else {
                pauseResumeBtn.setEnabled(true);
                pauseResumeBtn.setVisibility(View.VISIBLE);
                resetBtn.setEnabled(true);
                resetBtn.setVisibility(View.VISIBLE);
            }
        }
    }

    /**
     * Pause the timer
     */
    private void pauseTimer() {
        timeBuff += millisecondTime;
        handler.removeCallbacks(runnable);
        isPaused = true;
        if (isFinished) {
            pauseResumeBtn.setEnabled(false);
            pauseResumeBtn.setVisibility(View.INVISIBLE);
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(btn_size, btn_size);
            params.addRule(RelativeLayout.CENTER_HORIZONTAL);
            params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            int sizeInDP = 10;
            int marginInDp = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, sizeInDP, getResources().getDisplayMetrics());
            params.setMargins(0, 0, 0, marginInDp);
            saveBtn.setLayoutParams(params);
            saveBtn.setVisibility(View.VISIBLE);
            saveBtn.setEnabled(true);
        }
    }

    /**
     * Pause/Resumes the timer
     *
     * @param v - view object
     */
    public void onClickPauseResume(View v) {
        if (!isPaused) {
            pauseTimer();
            pauseResumeBtn.setText(getResources().getString(R.string.timer_resume));
        } else {
            startTimer();
            pauseResumeBtn.setText(getResources().getString(R.string.timer_pause));
        }
        isFinished = false;
    }

    public void onClickReset(View v) {
        Intent intent = getIntent();
        startActivity(intent);
        finish();
    }

    /**
     * Set on click for the marker buttons
     *
     * @param distance - current marker
     */
    public void onClickMarker(int distance) {
        // find current pace
        double currentPace = race.getCurrentPace(distance, updateTime);
        double pace = currentPace * 1000.0 * 60.0 * 60.0;
        if (race.getUnit().equals("mile")) {
            currentSpeedView.setText(String.format(Locale.getDefault(), "%.2f " + getString(R.string.pace_mile_per_hr)
                    , pace));
        } else {
            currentSpeedView.setText(String.format(Locale.getDefault(), "%.2f " + getString(R.string.pace_km_per_hr)
                    , pace));
        }

        if (distance == race.getMarkers()) {

            // finish
            resetBtn.setVisibility(View.GONE);
            resetBtn.setEnabled(false);
            if (race.getUnit().equalsIgnoreCase("mile") && race.getName().equalsIgnoreCase("Half Marathon")) {
                pace = pace / Race.MILE_CONVERSION;
            }
            if (race.getUnit().equalsIgnoreCase("mile") && race.getName().equalsIgnoreCase("Full Marathon")) {
                pace = pace / Race.MILE_CONVERSION;
            }
            BigDecimal bd = new BigDecimal(pace);
            bd = bd.setScale(2, RoundingMode.FLOOR);
            pace = bd.doubleValue();
            race.setAveragePace(pace);
            isFinished = true;
            scrollView.setVisibility(GONE);
            pauseTimer();
        } else {
            // increment marker
            long estimateTime = race.getEstimateTime(currentPace);
            if (race.getTime() != 0 && beatTime) {
                if (estimateTime < race.getTime()) {
                    Toast.makeText(this, "Great Job! Keep Up Your Pace...", Toast.LENGTH_SHORT).show();
                    estimatedTimeView.setTextColor(Color.GREEN);
                } else {
                    Toast.makeText(this, "Pick Up Your Pace!", Toast.LENGTH_SHORT).show();
                    Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                    // Vibrate for 500 milliseconds
                    v.vibrate(800);
                    estimatedTimeView.setTextColor(Color.RED);
                }
            }

            estimatedTimeView.setText(race.estimateTimeText(currentPace));
        }
    }

    /**
     * Store finish time and average pace
     */
    private void saveRace() {
        double pace = race.getAveragePace();
        long finishTime = updateTime;
        race.setAveragePace(pace);
        race.setTime(finishTime);
    }

    /**
     * Save button
     *
     * @param v - view object
     */
    public void onClickSave(View v) {

        final AlertDialog alertDialog = new AlertDialog.Builder(TimerActivity.this).create();
        alertDialog.setTitle("Activity Log");
        alertDialog.setMessage("Would you like to save this race?");
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                saveRace();
                final RecordDao recordDao = new RecordDao(TimerActivity.this);
                Calendar c = Calendar.getInstance();
                SimpleDateFormat df = new SimpleDateFormat("MMM. dd, yyyy", Locale.CANADA);
                String currentDate = df.format(c.getTime());
                final Record record = new Record(race.getAveragePace(), race.getTime(), currentDate, race.getId());
                if (race.addRecord(record)) {
                    recordDao.insert(record);
                } else {
                    dialog.dismiss();
                    AlertDialog warnDialog = new AlertDialog.Builder(TimerActivity.this).create();
                    warnDialog.setTitle("Activity Log");
                    warnDialog.setMessage("This will overwrite your previous records. Would you like to continue?");
                    warnDialog.setButton(AlertDialog.BUTTON_POSITIVE, "YES", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            recordDao.update(race.getWorstRecord().getId(), record);
                            recordDao.close();
                        }
                    });

                    warnDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            recordDao.close();
                        }
                    });
                    warnDialog.show();
                }
                TimerActivity.this.finish();
            }
        });

        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                TimerActivity.this.finish();
            }
        });
        alertDialog.show();
    }

    /**
     * Start button
     *
     * @param v - view object
     */
    public void onClickStart(View v) {
        // Hit start
        scrollView.setVisibility(View.VISIBLE);
        startTimer();
    }

    /**
     * Handle special start marker cases.
     */
    private void makeStartMarker() {
        // start 457 timer image change

        // within range of existing images
        if (raceId >= 4 && raceId <= 6)
        {
            startBtn.setText(""); // Clear start button text to prevent duplication
            startBtn.setBackground(getResources().getDrawable(startBackgroundArray[raceId]));
        }

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(btn_size, btn_size);
        params.addRule(RelativeLayout.CENTER_HORIZONTAL);
        params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        int sizeInDP = 10;
        int marginInDp = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, sizeInDP, getResources().getDisplayMetrics());
        params.setMargins(0, 0, 0, marginInDp);
        startBtn.setLayoutParams(params);
    }

    /**
     * Handle special finish marker
     * @param markerBtn - button object
     */
    private void makeFinishMaker(Button markerBtn) {
        if (raceId >= 4 && raceId <= 6) {
            markerBtn.setText("");
            markerBtn.setBackground(getResources().getDrawable(finishBackgroundArray[raceId]));
        }
    }


    /**
     * Create markers
     */
    private void makeMarkers() {
        final LinearLayout markers = (LinearLayout) findViewById(R.id.linearLayout_timer_markers);
        for (int id = 0; id <= race.getMarkers() + 1; id++) {
            final Button markerBtn = new Button(this);
            markerBtn.setText(race.getMarkerName(id));
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(btn_size, btn_size);
            params.setMargins(0, 0, BTN_MARGIN, 0);
            markerBtn.setLayoutParams(params);
            if (id == 0 || id == race.getMarkers() + 1) {
                markerBtn.setLayoutParams(new LinearLayout.LayoutParams(btn_size, btn_size));
                markerBtn.setBackground(getResources().getDrawable(R.drawable.bottom_button));
                markerBtn.setVisibility(View.INVISIBLE);
                markerBtn.setEnabled(false);
            } else {
                markerBtn.setId(id);
                markerBtn.setBackground(getResources().getDrawable(R.drawable.bottom_button));
                markerBtn.setTextColor(Color.WHITE);
                markerBtn.setTextSize(30);
            }

            if(race.getMarkerName(id).equalsIgnoreCase("FINISH")) {
                makeFinishMaker(markerBtn);
            }

            markerBtn.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    onClickMarker(markerBtn.getId());
                    scrollView.smoothScrollBy(markerBtn.getWidth() + BTN_MARGIN, 0);
                }
            });
            markers.addView(markerBtn);
        }
    }

    /**
     * Handle special case for crunch buttons.
     */
    public void makeCrunchMarkers() {
        final LinearLayout markers = (LinearLayout) findViewById(R.id.linearLayout_timer_markers);
        for (int id = 0; id <= race.getMarkers() + 1; id++) {
            final Button markerBtn = new Button(this);
            markerBtn.setText(race.getMarkerName(id));
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(btn_size, btn_size);
            params.setMargins(0, 0, BTN_MARGIN, 0);
            markerBtn.setLayoutParams(params);
            if (id == 0 || id == (race.getMarkers() + 1)) {
                markerBtn.setLayoutParams(new LinearLayout.LayoutParams(btn_size, btn_size));
                markerBtn.setBackground(getResources().getDrawable(R.drawable.bottom_button));
                markerBtn.setVisibility(View.INVISIBLE);
                markerBtn.setEnabled(false);
            } else {
                markerBtn.setId(id);
                markerBtn.setBackground(getResources().getDrawable(R.drawable.bottom_button));
                markerBtn.setTextColor(Color.WHITE);
                markerBtn.setTextSize(30);
//                switch (id) {
//                    case 1:
//                        markerBtn.setBackground(getResources().getDrawable(R.drawable.two));
//                        break;
//                    case 2:
//                        markerBtn.setBackground(getResources().getDrawable(R.drawable.three));
//                        break;
//                    case 3:
//                        markerBtn.setBackground(getResources().getDrawable(R.drawable.four));
//                        break;
//                    case 4:
//                        markerBtn.setBackground(getResources().getDrawable(R.drawable.five));
//                        break;
//                    case 5:
//                        markerBtn.setBackground(getResources().getDrawable(R.drawable.six));
//                        break;
//                    case 6:
//                        markerBtn.setBackground(getResources().getDrawable(R.drawable.seven));
//                        break;
//                    case 7:
//                        markerBtn.setBackground(getResources().getDrawable(R.drawable.eight));
//                        break;
//                    default:
//                        markerBtn.setBackground(getResources().getDrawable(R.drawable.nine));
//                }
            }

            if(race.getMarkerName(id).equalsIgnoreCase("FINISH")) {
                makeFinishMaker(markerBtn);
            }

            markerBtn.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    onClickMarker(markerBtn.getId());
                    scrollView.smoothScrollBy(markerBtn.getWidth() + BTN_MARGIN, 0);
                }
            });

            markers.addView(markerBtn);
        }
    }
}