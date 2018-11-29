package ca.sclfitness.keeppace;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class RaceActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        PreferenceHelper p = new PreferenceHelper(this);
        p.setTheme();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_race);

        if (getSupportActionBar() != null)
        {
            getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
            getSupportActionBar().setCustomView(R.layout.intermediate_menu);
            ((TextView) findViewById(R.id.textView_transition_title)).setText(R.string.title_race);
        }
    }

    public void onClickFive(View v) {
        Intent i = new Intent(this, PaceActivity.class);
        i.putExtra("type", 3);    // 5K Race
        startActivity(i);
    }

    public void onClickTen(View v) {
        Intent i = new Intent(this, PaceActivity.class);
        i.putExtra("type", 4);    // 10K Race
        startActivity(i);
    }

    public void onClickHalfMarathon(View v) {
        Intent i = new Intent(this, PaceActivity.class);
        i.putExtra("type", 5);    // Half Marathon
        startActivity(i);
    }

    public void onClickFullMarathon(View v) {
        Intent i = new Intent(this, PaceActivity.class);
        i.putExtra("type", 6);    // Full Marathon
        startActivity(i);
    }
}
