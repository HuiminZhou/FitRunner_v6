package com.example.huimin_zhou.fitrunner;

import android.app.Fragment;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.huimin_zhou.fitrunner.Database.DatabaseSource;
import com.example.huimin_zhou.fitrunner.Database.ExerciseEntry;
import com.example.huimin_zhou.fitrunner.Database.MySQLOpenHelper;
import com.example.huimin_zhou.fitrunner.Util.Global;
import com.example.huimin_zhou.fitrunner.Util.Parser;
import com.example.huimin_zhou.fitrunner.Util.ServerUtilities;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Lucidity on 17/1/17.
 */

public class WelcomeFragment extends Fragment implements Button.OnClickListener{
    private Spinner inputType = null;
    private Spinner activType = null;
    private DatabaseSource mDBSource;
    private final String MAP_KEY = "JSON_ARRAY";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
        View v = inflater.inflate(R.layout.fragment_welcome, container, false);
        Button btnStart = (Button) v.findViewById(R.id.btn_start);
        btnStart.setOnClickListener(this);
        Button btnSync = (Button) v.findViewById(R.id.btn_syn);
        btnSync.setOnClickListener(this);
        inputType = ((Spinner) v.findViewById(R.id.spinner_input));
        activType = ((Spinner) v.findViewById(R.id.spinner_activity));
        return v;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_start:
                int inputIndex = inputType.getSelectedItemPosition();
                int activIndex = activType.getSelectedItemPosition();
                String[] inputlist = getResources().getStringArray(R.array.entries_input);
                String[] activlist = getResources().getStringArray(R.array.entries_activity);
                Intent intent = null;
                Bundle bundle = new Bundle();
                switch (inputIndex) {
                    case 0: // Manual input
                        intent = new Intent(getActivity(), ManualActivity.class);
                        bundle.putString(MySQLOpenHelper.INPUT_TYPE, inputlist[inputIndex]);
                        bundle.putString(MySQLOpenHelper.ACTIVITY_TYPE, activlist[activIndex]);
                        intent.putExtras(bundle);
                        break;
                    case 1: // Map
                        intent = new Intent(getActivity(), MapActivity.class);
                        bundle.putBoolean(Global.KEY_ISSERVICE, true);
                        bundle.putBoolean(Global.KEY_ISMILE, Parser.isMile(getActivity()));
                        bundle.putString(MySQLOpenHelper.INPUT_TYPE, inputlist[inputIndex]);
                        bundle.putString(MySQLOpenHelper.ACTIVITY_TYPE, activlist[activIndex]);
                        intent.putExtras(bundle);
                        break;
                    case 2: // Automatic
                        intent = new Intent(getActivity(), MapActivity.class);
                        bundle.putBoolean(Global.KEY_ISSERVICE, true);
                        bundle.putBoolean(Global.KEY_ISMILE, Parser.isMile(getActivity()));
                        bundle.putString(MySQLOpenHelper.INPUT_TYPE, inputlist[inputIndex]);
                        bundle.putString(MySQLOpenHelper.ACTIVITY_TYPE,
                                getString(R.string.activity_unknown));
                        intent.putExtras(bundle);
                        break;
                }
                startActivity(intent);
                break;
            case R.id.btn_syn:
                mDBSource = new DatabaseSource(getActivity());
                //Log.d("syc", "before");
                new SyncTask().execute();
                break;
        }
    }

    private class SyncTask extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... params) {
            String uploadState = "";
            mDBSource.open();
            ArrayList<ExerciseEntry> entries = mDBSource.queryAll();
            mDBSource.close();

            try {
                JSONArray array = new JSONArray();
                for (ExerciseEntry entry : entries) {
                    JSONObject values = new JSONObject();
                    SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss MMM dd yyyy");
                    DecimalFormat decimalFormat = new DecimalFormat("#.##");
                    values.put(MySQLOpenHelper.ID,              String.valueOf(entry.getId()));
                    values.put(MySQLOpenHelper.INPUT_TYPE,      entry.getInputType());
                    values.put(MySQLOpenHelper.ACTIVITY_TYPE,   entry.getActivityType());
                    values.put(MySQLOpenHelper.DATE,            dateFormat.format(entry.getDate().getTime()));
                    values.put(MySQLOpenHelper.DURATION,        Parser.parseSecond(entry.getDuration()));
                    values.put(MySQLOpenHelper.DISTANCE,        decimalFormat.format(entry.getDistance()));
                    values.put(MySQLOpenHelper.CLIMB,           decimalFormat.format(entry.getClimb()));
                    values.put(MySQLOpenHelper.AVG_SPEED,       decimalFormat.format(entry.getAvgSpeed()));
                    values.put(MySQLOpenHelper.CALORIES,        String.valueOf(entry.getCalories()));
                    values.put(MySQLOpenHelper.HEART_RATE,      String.valueOf(entry.getHeart()));
                    values.put(MySQLOpenHelper.COMMENT,         String.valueOf(entry.getComment()));
                    values.put(MySQLOpenHelper.LOCATION,        Parser.LatLng2String(
                            entry.getLocationList()));
                    array.put(values);
                }
                Map<String, String> map = new HashMap<>();
                map.put(MAP_KEY, array.toString());
                //Log.d("syc", "post");
                ServerUtilities.post(MainActivity.SERVER_ADDR + "/add.do", map);
            } catch (IOException e1) {
                uploadState = "Sync failed: " + e1.getCause();
            } catch (JSONException e2) {
                uploadState = "Sync failed: " + e2.getCause();
            }
            return uploadState;
        }

        @Override
        protected void onPostExecute(String params) {
            Toast.makeText(getActivity(), "Sync", Toast.LENGTH_LONG).show();
        }
    }
}
