package com.example.huimin_zhou.fitrunner;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.example.huimin_zhou.fitrunner.Database.DatabaseSource;
import com.example.huimin_zhou.fitrunner.Database.ExerciseEntry;
import com.example.huimin_zhou.fitrunner.Database.MySQLOpenHelper;
import com.example.huimin_zhou.fitrunner.Util.Parser;
import com.example.huimin_zhou.fitrunner.Util.ServerUtilities;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


/**
 * Created by Lucidity on 17/2/1.
 */

public class HistoryEntryActivity extends AppCompatActivity {
    private long mID;
    private DatabaseSource mDBSource;
    private ExerciseEntry mEntry;

    @Override
    public void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
        setContentView(R.layout.activity_history);
        // get intent extras
        Intent intent = this.getIntent();
        mID = intent.getExtras().getLong(MySQLOpenHelper.ID);
        mDBSource = new DatabaseSource(this);
        mDBSource.open();
        mEntry = mDBSource.queryOne(mID);
        mDBSource.close();

        setView();
        // add toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_history);
        setSupportActionBar(toolbar);
    }

    // push the entry values to view
    private void setView() {
        EditText editText1 =  (EditText)findViewById(R.id.edit_input);
        EditText editText2 =  (EditText)findViewById(R.id.edit_activity);
        EditText editText3 =  (EditText)findViewById(R.id.edit_date);
        EditText editText4 =  (EditText)findViewById(R.id.edit_duration);
        EditText editText5 =  (EditText)findViewById(R.id.edit_distance);
        EditText editText6 =  (EditText)findViewById(R.id.edit_calories);
        EditText editText7 =  (EditText)findViewById(R.id.edit_heart);

        editText1.setText(mEntry.getInputType());
        editText2.setText(mEntry.getActivityType());
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss MMM dd yyyy");
        editText3.setText(dateFormat.format(mEntry.getDate().getTime()));
        editText4.setText(Parser.parseSecond(mEntry.getDuration()));
        editText5.setText(Parser.parseDistance(this, mEntry.getDistance()));
        editText6.setText("" + mEntry.getCalories() + " cals");
        editText7.setText("" + mEntry.getHeart() + " bpm");
    }

    /***************************** Delete Button *****************************/
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.actionbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                // delete entry in database
                mDBSource.open();
                mDBSource.delete(mEntry);
                mDBSource.close();
                new DeleteTask().execute();
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    // delete entry in website
    private class DeleteTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            try {
                Map<String, String> map = new HashMap<>();
                map.put("id_device", String.valueOf(mEntry.getId()));
                ServerUtilities.post(MainActivity.SERVER_ADDR + "/delete.do", map);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}
