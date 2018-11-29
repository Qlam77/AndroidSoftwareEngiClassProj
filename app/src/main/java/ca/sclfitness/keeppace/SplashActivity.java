package ca.sclfitness.keeppace;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        PreferenceHelper p = new PreferenceHelper(this);
        p.setTheme();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        loadingBuffer();
    }

    public void loadingBuffer() {
        new java.util.Timer().schedule(
                new java.util.TimerTask() {
                    @Override
                    public void run() {
                        SharedPreferences settings = getSharedPreferences("prefs", 0);
                        boolean firstRun = settings.getBoolean("firstRun", true);
                        if (firstRun) {
                            SharedPreferences.Editor editor = settings.edit();
                            editor.putBoolean("firstRun", false);
                            editor.apply();
                            Intent i = new Intent(SplashActivity.this, GetStartedActivity.class); //Activity to be launched For the First time
                            startActivity(i);
                            finish();
                        }
                        SplashActivity.this.finish();
                    }
                },
                5000
        );
    }
}
