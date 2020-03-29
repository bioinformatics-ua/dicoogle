/**
 * Copyright (C) 2014  Universidade de Aveiro, DETI/IEETA, Bioinformatics Group - http://bioinformatics.ua.pt/
 *
 * This file is part of Dicoogle/dicoogle.
 *
 * Dicoogle/dicoogle is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Dicoogle/dicoogle is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Dicoogle.  If not, see <http://www.gnu.org/licenses/>.
 */

package pt.ua.dicoogle.server.web.servlets.accounts;

import java.io.IOException;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import pt.ua.dicoogle.server.users.HashService;
import pt.ua.dicoogle.server.users.User;
import pt.ua.dicoogle.server.users.UsersStruct;
import pt.ua.dicoogle.server.web.utils.ResponseUtil;

/**
 * User Servlet for create, remove and consult user accounts
 *
 * @author Frederico Silva <fredericosilva@ua.pt>
 */
public class UserServlet extends HttpServlet {

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String pathInfo = req.getPathInfo();

        if (pathInfo == null) {
            ResponseUtil.sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "Username not provided");
            return;
        }

        String[] params = pathInfo.replaceFirst("^/", "").split("/");

        if (params.length < 1) {
            ResponseUtil.sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "Username not provided");
            return;
        }

        String usernameToRemove = params[0];
        boolean isRemoved = false;
        if (usernameToRemove != null && !usernameToRemove.equals("")) {
            isRemoved = UsersStruct.getInstance().removeUser(usernameToRemove);
        }

        ResponseUtil.simpleResponse(resp, "success", isRemoved);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String user = req.getParameter("username");
        String pass = req.getParameter("password");
        boolean admin = Boolean.parseBoolean(req.getParameter("admin"));
        boolean wasAdded = UsersStruct.getInstance().addUser(User.create(user, admin, pass));
        ResponseUtil.simpleResponse(resp, "success", wasAdded);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        Set<String> users = UsersStruct.getInstance().getUsernames();
        resp.setContentType("application/json");

        JSONObject jsonObject = new JSONObject();
        JSONArray usersArray = new JSONArray();
        for (String user : users) {
            JSONObject u = new JSONObject();
            u.put("username", user);
            usersArray.add(u);
        }

        jsonObject.put("users", usersArray);
        jsonObject.write(resp.getWriter());
    }

}
