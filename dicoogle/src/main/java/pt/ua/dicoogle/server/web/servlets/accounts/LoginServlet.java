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

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.sf.json.JSONObject;

import deletion.Authentication;
import deletion.LoggedIn;
import deletion.Session;

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
