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
import pt.ua.dicoogle.server.web.auth.Authentication;
import pt.ua.dicoogle.server.web.auth.Session;
import pt.ua.dicoogle.server.web.utils.ResponseUtil;

/**
 *
 * @author Frederico Silva <fredericosilva@ua.pt>
 */
public class LogoutServlet extends HttpServlet{

    private static final String TOKEN_HEADERNAME = "Authorization";

    @Deprecated
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        boolean logout = Session.logout(req);
        String token = req.getHeader(TOKEN_HEADERNAME);
        if (token != null && !token.equals("")) {
            Authentication.getInstance().logout(token);
        }
        ResponseUtil.simpleResponse(resp,"success", logout);
    }

}
