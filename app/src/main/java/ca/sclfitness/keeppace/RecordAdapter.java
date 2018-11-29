package ca.sclfitness.keeppace;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;
import java.util.Locale;

import ca.sclfitness.keeppace.model.Race;
import ca.sclfitness.keeppace.model.Record;

/**
 * @author Jason, Tzu Hsiang Chen
 * @since November 21, 2017
 */

public class RecordAdapter extends ArrayAdapter<Record> {

    private Context mContext;

    private Typeface font = Typeface.create("sans-serif", Typeface.NORMAL);

    public RecordAdapter(Context context, List<Record> records) {
        super(context, 0, records);
        mContext = context;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Record record = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.record_list_row, parent, false);
        }

        final TextView dateText = (TextView) convertView.findViewById(R.id.textView_recordList_date);
        final TextView paceText = (TextView) convertView.findViewById(R.id.textView_recordList_pace);
        final TextView timeText = (TextView) convertView.findViewById(R.id.textView_recordList_time);

        dateText.setTypeface(font);
        paceText.setTypeface(font);
        timeText.setTypeface(font);

        if (record != null) {
            dateText.setText(record.getDate());
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
            String unit = sharedPreferences.getString(mContext.getString(R.string.key_unit), "1");

            String unitText;
            if (unit.equals("2")) {
                unitText = mContext.getString(R.string.pace_mile_per_hr);
                paceText.setText(String.format(Locale.getDefault(), "%.2f %s", record.getAveragePace() * Race.MILE_CONVERSION, unitText));
            } else {
                unitText = mContext.getString(R.string.pace_km_per_hr);
                paceText.setText(String.format(Locale.getDefault(), "%.2f %s", record.getAveragePace(), unitText));
            }

            timeText.setText(record.timeTextFormat(record.getTime()));
        }

        return convertView;
    }
}
