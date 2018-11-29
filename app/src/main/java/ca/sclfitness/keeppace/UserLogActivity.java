package ca.sclfitness.keeppace;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.List;

import ca.sclfitness.keeppace.Dao.RaceDao;
import ca.sclfitness.keeppace.model.Race;

public class UserLogActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        PreferenceHelper p = new PreferenceHelper(this);
        p.setTheme();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_log);

    }

    @Override
    protected void onResume() {
        setRaceList();
        super.onResume();
    }

    public void setRaceList() {
        final ListView listView = (ListView) findViewById(R.id.listView_userLog_raceList);
        RaceDao raceDao = new RaceDao(UserLogActivity.this);
        List<Race> races = raceDao.findAllRaces();
        raceDao.close();
        UserLogAdapter adapter = new UserLogAdapter(UserLogActivity.this, races);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Race race = (Race) parent.getItemAtPosition(position);
                Intent i = new Intent(UserLogActivity.this, RecordsActivity.class);
                i.putExtra("raceId", race.getId());
                i.putExtra("raceName", race.getName());
                startActivity(i);
            }
        });
    }
}
