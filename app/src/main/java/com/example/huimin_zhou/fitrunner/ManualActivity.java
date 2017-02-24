package com.example.huimin_zhou.fitrunner;

import android.app.DialogFragment;
import android.app.ListActivity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.example.huimin_zhou.fitrunner.Database.DatabaseSource;
import com.example.huimin_zhou.fitrunner.Database.ExerciseEntry;
import com.example.huimin_zhou.fitrunner.Database.MySQLOpenHelper;


/**
 * Created by Lucidity on 17/1/17.
 */

public class ManualActivity extends ListActivity {
    DatabaseSource mDBSource = null;
    public static ExerciseEntry mEntry = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manual);

        // get input type and activity type
        Intent intent = this.getIntent();
        String inputType = intent.getExtras().getString(MySQLOpenHelper.INPUT_TYPE);
        String activType = intent.getExtras().getString(MySQLOpenHelper.ACTIVITY_TYPE);
        mEntry = new ExerciseEntry();
        mEntry.setInputType(inputType);
        mEntry.setActivityType(activType);

        // list view
        ArrayAdapter<String> mAdapter = new ArrayAdapter<String> (
                this,
                android.R.layout.simple_list_item_1,
                getResources().getStringArray(R.array.entries_item));
        setListAdapter(mAdapter);
        ListView mListView = getListView();
        AdapterView.OnItemClickListener mListener = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                onDialog(position);
            }
        };
        mListView.setOnItemClickListener(mListener);

        mDBSource = new DatabaseSource(this);
    }

    // trigger dialog
    public void onDialog(int position) {
        DialogFragment dialogFragment = DialogsFragment.newInstance(position);
        dialogFragment.show(getFragmentManager(), "DIALOG_FRAGMENT");
    }

    public void onSaveClick(View view) {
        new SaveToDB().execute(mEntry);
        finish();
    }
    public void onCancelClick(View view) {
        Toast.makeText(getApplicationContext(), "Cancelled", Toast.LENGTH_SHORT).show();
        finish();
    }

    // save current entry to database
    public class SaveToDB extends AsyncTask<ExerciseEntry, Void, String> {
        @Override
        protected String doInBackground(ExerciseEntry... entries) {
            mDBSource.open();
            int duration = entries[0].getDuration();
            if (duration != 0) {
                entries[0].setAvgSpeed((float) (entries[0].getDistance() * 60.0 / duration));
            }
            long id = mDBSource.insertEntry(entries[0]);
            mDBSource.close();
            return "" + id;

        }

        @Override
        protected void onPostExecute(String result) {
            Toast.makeText(getApplicationContext(), "Entry #" + result + " saved.", Toast.LENGTH_SHORT)
                    .show();
        }

    }
}
