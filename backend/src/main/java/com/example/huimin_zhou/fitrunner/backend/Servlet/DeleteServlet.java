package com.example.huimin_zhou.fitrunner.backend.Servlet;

import com.example.huimin_zhou.fitrunner.backend.Datastore.MyDatastore;
import com.example.huimin_zhou.fitrunner.backend.MessagingEndpoint;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by Lucidity on 17/2/22.
 */

public class DeleteServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    public void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException, ServletException {
        boolean isWebsite = true;
        String id = req.getParameter("id_website");
        if (id == null || id.length() == 0) {
            isWebsite = false;
            id = req.getParameter("id_device");
        }

        boolean isDeleted = MyDatastore.delete(id);
        // if delete from device, then no bothering send back the msg
        // otherwise, send id back to device
        if (isDeleted && isWebsite) {
            MessagingEndpoint msg = new MessagingEndpoint();
            msg.sendMessage(id);
        }
        resp.sendRedirect("/query.do");
    }

    public void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws IOException, ServletException {
        doGet(req, resp);
    }
}
