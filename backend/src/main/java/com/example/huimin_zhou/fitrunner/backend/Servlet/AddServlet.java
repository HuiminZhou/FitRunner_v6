package com.example.huimin_zhou.fitrunner.backend.Servlet;

import com.example.huimin_zhou.fitrunner.backend.Datastore.ExerciseEntry;
import com.example.huimin_zhou.fitrunner.backend.Datastore.MyDatastore;
import com.example.huimin_zhou.fitrunner.backend.Global;
import com.example.huimin_zhou.fitrunner.backend.MessagingEndpoint;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by Lucidity on 17/2/22.
 */

public class AddServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    public void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException, ServletException {
        JSONParser parser = new JSONParser();
        JSONArray array = null;
        try {
            String json = req.getParameter(Global.MAP_KEY);
            if (json != null) {
                array = (JSONArray) parser.parse(json);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        int size = array == null ? 0 : array.size();
        // get each entry and save to datastore
        for (int i = 0; i < size; ++i) {
            JSONObject object = (JSONObject) array.get(i);
            String id = (String) object.get(ExerciseEntry.ID);
            String inputType = (String) object.get(ExerciseEntry.INPUT_TYPE);
            String activType = (String) object.get(ExerciseEntry.ACTIVITY_TYPE);
            String dateTime = (String) object.get(ExerciseEntry.DATE);
            String duration = (String) object.get(ExerciseEntry.DURATION);
            String distance = (String) object.get(ExerciseEntry.DISTANCE);
            String climb = (String) object.get(ExerciseEntry.CLIMB);
            String avgSpeed = (String) object.get(ExerciseEntry.AVG_SPEED);
            String calorie = (String) object.get(ExerciseEntry.CALORIES);
            String heartRate = (String) object.get(ExerciseEntry.HEART_RATE);
            String comment = (String) object.get(ExerciseEntry.COMMENT);
            ExerciseEntry entry = new ExerciseEntry(id, inputType, activType, dateTime, duration,
                    distance, climb, avgSpeed, calorie, heartRate, comment);
            MyDatastore.add(entry);
        }

        getServletContext().getRequestDispatcher("/query.do").forward(req, resp);
    }

    public void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws IOException, ServletException {
        doGet(req, resp);
    }
}
