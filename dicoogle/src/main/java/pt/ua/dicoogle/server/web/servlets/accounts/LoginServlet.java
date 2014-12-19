/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pt.ua.dicoogle.server.web.servlets.accounts;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.sf.json.JSONObject;

import pt.ua.dicoogle.server.web.auth.Authentication;
import pt.ua.dicoogle.server.web.auth.LoggedIn;
import pt.ua.dicoogle.server.web.auth.Session;

/**
 *
 * @author Frederico Silva <fredericosilva@ua.pt>
 */
public class LoginServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        //Try login
        LoggedIn mLoggedIn = Session.servletLogin(req, resp, true);//auth.login(user, pass);

        if (mLoggedIn == null) {
            resp.sendError(401, "Login failed");
            return;
        }

        JSONObject json_resp = new JSONObject();
        json_resp.put("user", mLoggedIn.getUserName());
        json_resp.put("admin", mLoggedIn.isAdmin());

        //Set response content type
        resp.setContentType("application/json");

        //Write response
        json_resp.write(resp.getWriter());
    }

}
