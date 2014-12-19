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

package pt.ua.dicoogle.server.web;

import java.io.IOException;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 *
 * @author psytek
 */
public class IndexControlServlet extends HttpServlet {
    
    
    
    public IndexControlServlet(){
        
	}

    @Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException{
		//prepares the request with the state variables required by the JSP
        request.setAttribute("isIndexing", false);
        request.setAttribute("indexingProgress", 0);

        System.err.println(request);

        
        RequestDispatcher view = request.getRequestDispatcher("indexamos.jsp");
        System.err.println(getServletContext());
        if(view==null){
            System.err.println("Null values motherfucker...");
            response.sendError(500, "beacuse fuck you, thats why");
            return;
        }
        view.forward(request, response);
        //System.err.println("ENDING DOGET SERVLET");
        }
    
    
    @Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException{
        doGet(request,response);
    }
}
