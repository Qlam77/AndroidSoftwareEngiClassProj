package ca.sclfitness.keeppace;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ShareActionProvider;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import ca.sclfitness.keeppace.Dao.RaceDao;
import ca.sclfitness.keeppace.Dao.RecordDao;
import ca.sclfitness.keeppace.model.Race;
import ca.sclfitness.keeppace.model.Record;

import static java.lang.Integer.parseInt;

public class RecordsActivity extends AppCompatActivity {

    private ShareActionProvider shareActionProvider;
    private RecordAdapter adapter;
    private List<Record> records;
    private ListView recordList;
    private Race race;
    private String raceName;
    private String m_Date = "";
    private double inputTime;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        PreferenceHelper p = new PreferenceHelper(this);
        p.setTheme();
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        setContentView(R.layout.activity_records);
        recordList = (ListView) findViewById(R.id.listView_records_recordList);
        int raceId = getIntent().getIntExtra("raceId", -1);
        raceName = getIntent().getStringExtra("raceName");
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(getResources().getString(R.string.app_title) + " - " + raceName);
        }

        if (raceId > 0) {
            RecordDao recordDao = new RecordDao(this);
            records = recordDao.findRecordsByRaceId(raceId);
            recordDao.close();
            if (records != null) {
                adapter = new RecordAdapter(this, records);
                recordList.setAdapter(adapter);
            } else {
                records = new ArrayList<>();
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        //updateUI(currentUser);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.getMenuInflater().inflate(R.menu.record_menu, menu);
        MenuItem menuItem = menu.findItem(R.id.upload_action);
        shareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);
        setShareActionProvider(recordFormat());
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.upload_to_cloud:
                final AlertDialog cloudDialog = new AlertDialog.Builder(RecordsActivity.this).create();
                cloudDialog.setTitle("Cloud Backup");
                cloudDialog.setMessage("Would you upload or download records?");
                cloudDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Download", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mAuth.signInWithEmailAndPassword("KP_Testing@somewhere.com", "KP_Test")
                                .addOnCompleteListener(RecordsActivity.this, new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if (task.isSuccessful()) {
                                            FirebaseUser user = mAuth.getCurrentUser();
                                            showToast("Successful Log In");
                                            final RecordDao recordDao = new RecordDao(RecordsActivity.this);
                                            for (Record r : records) {
                                                recordDao.delete(r.getId());
                                            }
                                            recordDao.close();

                                            DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
                                            mRootRef.child(user.getUid()).child(raceName).addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                    RaceDao raceDao = new RaceDao(RecordsActivity.this);
                                                    race = raceDao.findRaceByName(raceName);
                                                    raceDao.close();
                                                    for (DataSnapshot snapshot: dataSnapshot.getChildren()) {
                                                        RecordDao recordDao = new RecordDao(RecordsActivity.this);
                                                        double mAveragePace = Double.parseDouble(snapshot.child("mAveragePace").getValue().toString());
                                                        long mTime = Long.parseLong(snapshot.child("mTime").getValue().toString());
                                                        int mRaceId = Integer.parseInt(snapshot.child("mRaceId").getValue().toString());
                                                        String mDate = snapshot.child("mDate").getValue().toString();

                                                        Record record = new Record(mAveragePace, mTime, mDate, mRaceId);

                                                        if (race.addRecord(record)) {
                                                            recordDao.insert(record);
                                                        }
                                                    }
                                                    recordDao.close();
                                                    if (dataSnapshot.getChildrenCount() != 0) {
                                                        showToast("Downloaded backup");
                                                        RecordsActivity.this.finish();
                                                    } else {
                                                        showToast("Nothing to download");
                                                    }
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                                }
                                            });
                                        } else {
                                            showToast("Not Logged In");
                                        }
                                    }
                                });
                        dialog.dismiss();
                    }
                });

                cloudDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Upload", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mAuth.signInWithEmailAndPassword("KP_Testing@somewhere.com", "KP_Test")
                                .addOnCompleteListener(RecordsActivity.this, new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if (task.isSuccessful()) {
                                            FirebaseUser user = mAuth.getCurrentUser();
                                            showToast("Successful Log In");
                                            DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
                                            DatabaseReference dataRef = mRootRef.child(user.getUid());
                                            DatabaseReference dummyRef = dataRef.child(raceName);
                                            dataRef.child(raceName).removeValue();
                                            for (int i = 0; i < records.size(); i++) {
                                                Record record = records.get(i);
                                                dummyRef.child("record" + Integer.toString(i)).child("mId").setValue(record.getId());
                                                dummyRef.child("record" + Integer.toString(i)).child("mAveragePace").setValue(record.getAveragePace());
                                                dummyRef.child("record" + Integer.toString(i)).child("mTime").setValue(record.getTime());
                                                dummyRef.child("record" + Integer.toString(i)).child("mRaceId").setValue(record.getRaceId());
                                                dummyRef.child("record" + Integer.toString(i)).child("mDate").setValue(record.getDate());
                                            }
                                        } else {
                                            showToast("Not Logged In");
                                        }
                                    }
                                });
                        dialog.dismiss();
                        if (records.size() != 0) {
                            showToast("Uploaded backup");
                        } else {
                            showToast("Nothing to upload");
                        }
                    }
                });

                cloudDialog.show();
                return true;
            case R.id.delete_action:
                final AlertDialog alertDialog = new AlertDialog.Builder(RecordsActivity.this).create();
                alertDialog.setTitle("Clear Records");
                alertDialog.setMessage("Would you like to clear all records?");
                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        RecordDao recordDao = new RecordDao(RecordsActivity.this);
                        for (Record r : records) {
                            recordDao.delete(r.getId());
                        }
                        recordDao.close();
                        adapter.clear();
                        adapter.notifyDataSetChanged();
                        dialog.dismiss();
                    }
                });

                alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                alertDialog.show();
                return true;
            case R.id.add_action:
                RaceDao raceDao = new RaceDao(RecordsActivity.this);
                race = raceDao.findRaceByName(raceName);
                raceDao.close();
                final AlertDialog alertDialog1 = new AlertDialog.Builder(RecordsActivity.this).create();
                alertDialog1.setTitle("Add Time");
                alertDialog1.setMessage("Input your finish time.");
                final EditText input = new EditText(this);
                input.setGravity(Gravity.CENTER_HORIZONTAL);
                input.setHint("hh:mm:ss");
                input.setInputType(InputType.TYPE_CLASS_TEXT);
                alertDialog1.setView(input);

                alertDialog1.setButton(AlertDialog.BUTTON_POSITIVE, "Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Pace
                        String text = input.getText().toString();
                        if(!text.isEmpty())
                            try
                            {
                                String[] time = text.split("\\:");
                                int hours = parseInt(time[0]);
                                int mins = parseInt(time[1]);
                                int secs = parseInt(time[2]);
                                inputTime = (hours * 3600 * 1000) + (mins * 60 * 1000) + (secs * 1000);

                            } catch (Exception e) {
                                dialog.dismiss();
                            }

//                      race.setAveragePace(race.getMarkers() / inputTime);
                        race.setAveragePace((race.getMarkers() / inputTime) * 1000.0 * 60.0 * 60.0);

                        final RecordDao recordDao = new RecordDao(RecordsActivity.this);
                        Calendar c = Calendar.getInstance();
                        SimpleDateFormat df = new SimpleDateFormat("MMM. dd, yyyy", Locale.CANADA);
                        String currentDate = df.format(c.getTime());



                        final Record record = new Record(race.getAveragePace(), (long) inputTime, currentDate, race.getId());

                        if (race.addRecord(record)) {
                            recordDao.insert(record);
                        } else {
                            dialog.dismiss();
                            AlertDialog warnDialog = new AlertDialog.Builder(RecordsActivity.this).create();
                            warnDialog.setTitle("Add Time");
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
                        RecordsActivity.this.finish();
                    }
                });

                alertDialog1.setButton(AlertDialog.BUTTON_NEGATIVE, "Close", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        RecordsActivity.this.finish();
                    }
                });
                alertDialog1.show();
                return true;


            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void setShareActionProvider(String text) {
        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("text/plain");
        i.putExtra(Intent.EXTRA_TEXT, text);
        shareActionProvider.setShareIntent(i);
    }

    private String recordFormat() {
        String raceName = getIntent().getStringExtra("raceName");
        String recordsShareText = raceName + "\n";
        for (Record record : records) {
            String header = "DATE";
            String temp = record.getDate() + "\n" + record.getAveragePace() + " km/hr\n" + record.timeTextFormat(record.getTime()) + "\n";
            recordsShareText += temp;
        }

        return recordsShareText;
    }

    public void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
}
