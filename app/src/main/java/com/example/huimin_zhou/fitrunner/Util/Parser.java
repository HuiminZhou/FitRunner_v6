package com.example.huimin_zhou.fitrunner.Util;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.preference.PreferenceManager;

import com.example.huimin_zhou.fitrunner.R;
import com.google.android.gms.maps.model.LatLng;

import java.nio.ByteBuffer;
import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * Created by Lucidity on 17/2/5.
 */

public class Parser {
    // return if current preference is mile
    public static boolean isMile(Context context) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);

        String[] entries = context.getResources().getStringArray(R.array.entries_unit);
        String[] values = context.getResources().getStringArray(R.array.entries_unit_values);
        if (sharedPref.getString(context.getString(R.string.key_unit_preference), entries[0])
                .equals(values[1])) {
            return true;
        }
        return false;
    }

    // parse mile based distance to mile or kilometer
    public static String parseDistance(boolean isMile, float dist) {
        DecimalFormat decimalFormat = new DecimalFormat("#.##");
        if (isMile) {
            if (dist == 0) {
                return "0 Miles";
            }
            return String.valueOf(decimalFormat.format(dist)) + " Miles";
        } else {
            if (dist == 0) {
                return "0 Kilometers";
            }
            return String.valueOf(decimalFormat.format(dist * 1.60934)) + " Kilometers";
        }
    }

    // parse mile based distance to mile or kilometer
    public static String parseDistance(Context context, float dist) {
        DecimalFormat decimalFormat = new DecimalFormat("#.##");
        if (isMile(context)) {
            if (dist == 0) {
                return "0 Miles";
            }
            return decimalFormat.format(dist) + " Miles";
        } else {
            if (dist == 0) {
                return "0 Kilometers";
            }
            return decimalFormat.format(dist * 1.60934) + " Kilometers";
        }
    }

    // parse mile based speed to mile or kilometer
    public static String parseSpeed(boolean isMile, float speed) {
        DecimalFormat decimalFormat = new DecimalFormat("#.##");
        if (isMile) {
            if (Float.isNaN(speed)) {
                return "0 m/h";
            }
            return decimalFormat.format(speed) + " m/h";
        } else {
            if (Float.isNaN(speed)) {
                return "0 km/h";
            }
            speed *= 1.60934;
            return decimalFormat.format(speed) + " km/h";
        }
    }

    // parse (latitude, longitude) to distance with mile unit
    public static double parseLatLng2Distance (Location pre, Location cur) {
        return cur.distanceTo(pre) / 1000 / 1.60934; // meter to mile
    }

    // parse (latitude, longitude) arraylist to byte[]
    public static byte[] parseLatLng2Byte(ArrayList<LatLng> arrayList) {
        int size = arrayList.size();
        ByteBuffer buffer = ByteBuffer.allocate(size * 16);
        for (int i = 0; i < size; ++i) {
            buffer.putDouble(arrayList.get(i).latitude);
            buffer.putDouble(arrayList.get(i).longitude);
        }
        return buffer.array();
    }

    // parse byte[] to (latitude, longitude) arraylist
    public static ArrayList<LatLng> parseByte2Latlng(byte[] bytes) {
        ArrayList<LatLng> arrayList = new ArrayList<>();
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        while (buffer.remaining() > 0) {
            arrayList.add(new LatLng(buffer.getDouble(), buffer.getDouble()));
        }
        return arrayList;
    }

    // parse Location to (latitude, longitude)
    public static LatLng Location2LatLng(Location location) {
        return new LatLng(location.getLatitude(), location.getLongitude());
    }

    // parse Location to a Location with only latitude and longitude
    public static Location Location2Location(Location loc) {
        Location location = new Location("");
        location.setLatitude(loc.getLatitude());
        location.setLongitude(loc.getLongitude());
        return location;
    }

    // parse seconds if exceeding 60
    public static String parseSecond(int second) {
        if (second < 60) {
            return second + " secs";
        } else {
            return second / 60 + " mins " + second % 60 + " secs";
        }
    }

    public static ArrayList<String> LatLng2String(ArrayList<LatLng> arrayList) {
        ArrayList<String> res = new ArrayList<>();
        for (LatLng item : arrayList) {
            res.add(String.valueOf(item.latitude));
            res.add(String.valueOf(item.longitude));
        }
        return res;
    }
}
