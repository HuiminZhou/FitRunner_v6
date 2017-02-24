package com.example.huimin_zhou.fitrunner.backend.Servlet;

import com.example.huimin_zhou.fitrunner.backend.Datastore.ExerciseEntry;
import com.example.huimin_zhou.fitrunner.backend.Datastore.MyDatastore;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by Lucidity on 17/2/22.
 */

public class QueryServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    public void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException, ServletException {
        ArrayList<ExerciseEntry> entries = MyDatastore.query("");

        resp.setContentType("text/html");
        PrintWriter writer = resp.getWriter();
        writer.write(
                "<html><head>" +
                "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=ISO-8859-1\">\n" +
                "<title>Result List</title>" +
                "<style>table, th, td {border: 1px solid black;}</style>" +
                "</head><body>" +
                "<h2>Exercise Entries</h2>" +
                "<table>\n");
        writer.write(
                "<tr>" +
                "<th>ID<th>InputType<th>ActivityType<th>DateTime<th>Duration<th>Distance" +
                "<th>AvgSpeed<th>Calorie<th>Climb<th>HeartRate<th>Comment<th>" +
                "</tr>");

        // write entries to website
        int size = entries.size();
        for (int i = 0; i < size; ++i) {
            ExerciseEntry entry = entries.get(i);
            writer.write(
                    "<tr>" +
                    "<td>" + entry.mId +
                    "<td>" + entry.mInputType +
                    "<td>" + entry.mActivityType +
                    "<td>" + entry.mDate +
                    "<td>" + entry.mDuration +
                    "<td>" + entry.mDistance +
                    "<td>" + entry.mAvgSpeed +
                    "<td>" + entry.mCalories +
                    "<td>" + entry.mClimb +
                    "<td>" + entry.mHeart +
                    "<td>" + entry.mComment +
                    "<td> <a href =\"/delete.do?id_website=" + entry.mId + "\"> Delete </a>" +
                    "</tr>");
        }
        writer.write("</table></body></html>");
    }

    public void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws IOException, ServletException {
        doGet(req, resp);
    }
}
