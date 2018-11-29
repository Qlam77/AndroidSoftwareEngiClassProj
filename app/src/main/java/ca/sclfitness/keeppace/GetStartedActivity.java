package ca.sclfitness.keeppace;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.TextView;

public class GetStartedActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        PreferenceHelper p = new PreferenceHelper(this);
        p.setTheme();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_started);

        ((TextView) findViewById(R.id.getStarted)).setMovementMethod(LinkMovementMethod.getInstance());
        ((TextView) findViewById(R.id.getStarted)).setText(Html.fromHtml(getResources().getString(R.string.TOS)));
    }

    public void onStart(View v) {
        finish();
    }
}

