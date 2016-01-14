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
package pt.ua.dicoogle.server.web.servlets;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.restlet.Restlet;
import org.restlet.ext.servlet.ServletAdapter;

/** A servlet holding a Restlet.
 *
 * @author Eduardo Pinho <eduardopinho@ua.pt>
 */
public class RestletHttpServlet extends HttpServlet {

    private ServletAdapter adapter;
    private final Restlet restlet;

    public RestletHttpServlet(Restlet restlet) {
        this.restlet = restlet;
    }

    @Override
    public void init() throws ServletException {
        super.init();
        this.adapter = new ServletAdapter(getServletContext());
        this.restlet.setContext(this.adapter.getContext());
        this.adapter.setNext(this.restlet);
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.adapter.service(req, resp);
    }

}
